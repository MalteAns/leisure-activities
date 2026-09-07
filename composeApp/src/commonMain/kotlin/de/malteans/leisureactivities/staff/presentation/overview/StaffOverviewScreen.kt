package de.malteans.leisureactivities.staff.presentation.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.leisureactivities.core.presentation.components.CustomTopBar
import de.malteans.leisureactivities.core.presentation.util.SnackbarManager
import de.malteans.leisureactivities.core.presentation.util.currentPlatform
import de.malteans.leisureactivities.signUp.presentation.components.CustomPullToRefreshBox
import de.malteans.leisureactivities.staff.presentation.overview.components.CompactActivityItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
                is StaffOverviewAction.OnEditActivity -> onModifyActivity(action.activityId)
                is StaffOverviewAction.OnShowActivity -> onShowActivity(action.activityId)
                else -> viewModel.onAction(action)
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

    LaunchedEffect(state.deletingActivitiesError) {
        state.deletingActivitiesError?.let { error ->
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val animationDuration = 150
                val enterAnimation = expandIn(
                    animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDuration),
                    expandFrom = Alignment.Center,
                )
                val exitAnimation = shrinkOut(
                    animationSpec = tween(durationMillis = animationDuration),
                    shrinkTowards = Alignment.Center,
                )
                SmallFloatingActionButton(
                    onClick = { onAction(StaffOverviewAction.OnDeletingActivitiesChange(!state.deletingActivities)) },
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    AnimatedVisibility(!state.deletingActivities, enter = enterAnimation, exit = exitAnimation) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(Res.string.activate_deleting)
                        )
                    }
                    AnimatedVisibility(state.deletingActivities, enter = enterAnimation, exit = exitAnimation) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.cancel_deleting)
                        )
                    }
                }
                val defaultColor = FloatingActionButtonDefaults.containerColor
                val errorColor = MaterialTheme.colorScheme.errorContainer
                val containerColor = remember { androidx.compose.animation.Animatable(defaultColor) }
                LaunchedEffect(state.deletingActivities) {
                    containerColor.animateTo(
                        targetValue = if (state.deletingActivities) errorColor else defaultColor,
                        animationSpec = tween(durationMillis = animationDuration * 2)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        focusManager.clearFocus()
                        if (!state.deletingActivities)
                            onAction(StaffOverviewAction.OnCreateActivity)
                        else if (!state.deletingActivitiesInProgress && state.activityIdsToDelete.isNotEmpty())
                            onAction(StaffOverviewAction.OnSubmitDelete)
                    },
                    containerColor = containerColor.value,
                ) {
                    AnimatedVisibility(!state.deletingActivities, enter = enterAnimation, exit = exitAnimation) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(Res.string.create_activity)
                        )
                    }
                    AnimatedVisibility(state.deletingActivities, enter = enterAnimation, exit = exitAnimation) {
                        if (!state.deletingActivitiesInProgress) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = stringResource(Res.string.delete_activities)
                            )
                        } else {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                }
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
                if (currentPlatform().isDesktop) {
                    IconButton(
                        onClick = { onAction(StaffOverviewAction.RefreshActivities) },
                        enabled = !(state.loadingActivities || state.deletingActivitiesInProgress),
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        if (!(state.loadingActivities || state.deletingActivitiesInProgress)) {
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
                isRefreshing = state.loadingActivities || state.deletingActivitiesInProgress,
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
                            onClick = { onAction(StaffOverviewAction.OnShowActivity(activity.id)) },
                            onEditClick = { onAction(StaffOverviewAction.OnEditActivity(activity.id)) },
                            onLongClick = { onAction(StaffOverviewAction.OnSelectActivityToDelete(activity.id)) },
                            showCheckBox = state.deletingActivities,
                            checkBoxValue = state.activityIdsToDelete.contains(activity.id),
                            onCheckBoxValueChange = { isChecked ->
                                if (state.deletingActivities && !state.deletingActivitiesInProgress) {
                                    if (isChecked) onAction(StaffOverviewAction.OnSelectActivityToDelete(activity.id))
                                    else onAction(StaffOverviewAction.OnDeselectActivityToDelete(activity.id))
                                }
                            },
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }
        }
    }
}