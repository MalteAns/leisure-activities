package de.malteans.sosactivities.signUp.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.components.CustomTopBar
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.core.presentation.util.currentPlattform
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.signUp.presentation.components.ActivityItem
import de.malteans.sosactivities.signUp.presentation.components.CustomPullToRefreshBox
import de.malteans.sosactivities.signUp.presentation.components.SignOutDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sosactivities.composeapp.generated.resources.*

@Composable
fun SignUpOverviewScreenRoot(
    viewModel: SignUpViewModel = koinViewModel(),
    showDrawer: (Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignUpOverviewScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is SignUpOverviewAction.ShowDrawer -> showDrawer(action.show)
                else -> viewModel.onOverviewAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpOverviewScreen(
    state: SignUpState,
    onAction: (SignUpOverviewAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.loadingActivitiesError) {
        state.loadingActivitiesError?.let { error ->
            scope.launch(Dispatchers.IO) {
                SnackbarManager.showThrowableSnackbar(error)
                onAction(SignUpOverviewAction.ClearError)
            }
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        onAction(SignUpOverviewAction.RefreshCurrentActivities)
    }

    LaunchedEffect(state.loadingActivities) {
        if (!state.loadingActivities) {
            pullToRefreshState.animateToHidden()
        }
    }

    var signOutActivity by remember { mutableStateOf<ActivityWithImageUrl?>(null) }
    signOutActivity?.let { activity ->
        SignOutDialog(
            activity = activity,
            onConfirm = {
                onAction(SignUpOverviewAction.SignOutFromActivity(activity.id))
                signOutActivity = null
            },
            onDismiss = {
                signOutActivity = null
            }
        )
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.sign_up_overview),
                onOpenDrawer = { onAction(SignUpOverviewAction.ShowDrawer(true)) }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { newValue ->
                        onAction(SignUpOverviewAction.OnSearchQueryChange(newValue.replace("\n", "")))
                    },
                    label = { Text(text = stringResource(Res.string.search)) },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = state.searchQuery.isNotEmpty(),
                            enter = expandIn(expandFrom = Alignment.Center),
                            exit = shrinkOut(shrinkTowards = Alignment.Center)
                        ) {
                            IconButton({ onAction(SignUpOverviewAction.OnSearchQueryChange("")) } ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.empty_search)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                )
                if (currentPlattform().isDesktop) {
                    IconButton(
                        onClick = { onAction(SignUpOverviewAction.RefreshCurrentActivities) },
                        enabled = !state.loadingActivities,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        if (!state.loadingActivities) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(Res.string.refresh),
                            )
                        } else {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            CustomPullToRefreshBox(
                isRefreshing = state.loadingActivities,
                onRefresh = { onAction(SignUpOverviewAction.RefreshCurrentActivities) },
                state = pullToRefreshState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    verticalArrangement = spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.currentActivities) { activity ->
                        ActivityItem(
                            activity = activity,
                            onButtonClicked = {
                                when (activity.signedUp) {
                                    false -> onAction(SignUpOverviewAction.SignUpForActivity(activity.id))
                                    true -> signOutActivity = activity
                                    null -> /* should not happen */ Unit
                                }
                            },
                            onTTS = if (state.ttsEnabled) { uiText ->
                                onAction(SignUpOverviewAction.OnTTS(uiText))
                            } else null,
                        )
                    }
                }
            }
        }
    }
}