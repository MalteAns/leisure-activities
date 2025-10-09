package de.malteans.sosactivities.staff.presentation.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.components.CustomTopBar
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.core.presentation.util.currentPlattform
import de.malteans.sosactivities.signUp.presentation.components.CustomPullToRefreshBox
import de.malteans.sosactivities.staff.presentation.overview.components.CompactActivityItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sosactivities.composeapp.generated.resources.*

@Composable
fun StaffOverviewScreenRoot(
    viewModel: StaffOverviewViewModel = koinViewModel(),
    showDrawer: (Boolean) -> Unit,
    onModifyActivity: (activityId: String?) -> Unit,
    onShowActivity: (activityId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StaffOverviewScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is StaffOverviewAction.ShowDrawer -> showDrawer(action.show)
                StaffOverviewAction.OnCreateActivity -> onModifyActivity(null)
                is StaffOverviewAction.OnModifyActivity -> onModifyActivity(action.activityId)
                is StaffOverviewAction.OnShowActivity -> onShowActivity(action.activityId)
                else -> viewModel.onOverviewAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffOverviewScreen(
    state: StaffOverviewState,
    onAction: (StaffOverviewAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.loadingActivitiesError) {
        state.loadingActivitiesError?.let { error ->
            scope.launch(Dispatchers.IO) {
                SnackbarManager.showThrowableSnackbar(error)
                onAction(StaffOverviewAction.ClearError)
            }
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        onAction(StaffOverviewAction.RefreshActivities)
    }

    LaunchedEffect(state.loadingActivities) {
        if (!state.loadingActivities) {
            pullToRefreshState.animateToHidden()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.staff_area),
                onOpenDrawer = { onAction(StaffOverviewAction.ShowDrawer(true)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton({ onAction(StaffOverviewAction.OnCreateActivity)} ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_activity)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row( // Search Row
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { newValue ->
                        onAction(StaffOverviewAction.OnSearchQueryChange(newValue.replace("\n", "")))
                    },
                    label = { Text(text = stringResource(Res.string.search)) },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = state.searchQuery.isNotEmpty(),
                            enter = expandIn(expandFrom = Alignment.Center),
                            exit = shrinkOut(shrinkTowards = Alignment.Center)
                        ) {
                            IconButton({ onAction(StaffOverviewAction.OnSearchQueryChange("")) } ) {
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
                        onClick = { onAction(StaffOverviewAction.RefreshActivities) },
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
                onRefresh = { onAction(StaffOverviewAction.RefreshActivities) },
                state = pullToRefreshState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    verticalArrangement = spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.activitiesToShow) { activity ->
                        CompactActivityItem(
                            activity = activity,
                            onClick = { onAction(StaffOverviewAction.OnShowActivity(activity.id)) }
                        )
                    }
                }
            }
        }
    }
}