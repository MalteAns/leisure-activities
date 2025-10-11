package de.malteans.sosactivities.staff.presentation.activityDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.components.CustomTopBar
import de.malteans.sosactivities.core.presentation.components.ImageWithLoading
import de.malteans.sosactivities.core.presentation.util.toDateTimeString
import de.malteans.sosactivities.core.presentation.util.toTimeString
import de.malteans.sosactivities.signUp.presentation.components.DetailsInfoItem
import de.malteans.sosactivities.staff.presentation.activityDetails.components.ParticipantListItem
import de.malteans.sosactivities.themes.containerColor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sosactivities.composeapp.generated.resources.*
import kotlin.time.ExperimentalTime

@Composable
fun ActivityDetailsScreenRoot(
    viewModel: ActivityDetailsViewModel = koinViewModel(),
    activityId: String,
    onEditActivity: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(activityId) {
        viewModel.setActivityId(activityId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ActivityDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ActivityDetailsAction.OnEditActivity -> onEditActivity()
                ActivityDetailsAction.OnNavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun ActivityDetailsScreen(
    state: ActivityDetailsState,
    onAction: (ActivityDetailsAction) -> Unit,
) {
    val focusManger = LocalFocusManager.current
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.selectedTabIndex) {
        focusManger.clearFocus()
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState) {
        focusManger.clearFocus()
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onAction(ActivityDetailsAction.OnTabSelected(page))
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = state.currentActivity?.title ?: "",
                subtitle = state.currentActivity?.startsAt?.toDateTimeString()
                    ?.plus(state.currentActivity.endsAt?.let { " - ${it.toTimeString()}" } ?: ""),
                navigationIcon = {
                    IconButton({ onAction(ActivityDetailsAction.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    FilledIconButton(onClick = { onAction(ActivityDetailsAction.OnEditActivity) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.edit)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        if (state.currentActivity == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManger.clearFocus() }) }
                .fillMaxSize()
                .padding(8.dp)
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = state.selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                    )
                },
                containerColor = MaterialTheme.colorScheme.containerColor,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ))
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = state.selectedTabIndex == 0,
                    onClick = { onAction(ActivityDetailsAction.OnTabSelected(0)) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(Res.string.information),
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 12.dp)
                    )
                }
                Tab(
                    selected = state.selectedTabIndex == 1,
                    onClick = { onAction(ActivityDetailsAction.OnTabSelected(1)) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(Res.string.participants),
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 12.dp)
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.containerColor,
                        shape = MaterialTheme.shapes.medium.copy(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(0.dp),
                        )
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> { // Information Tab
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                        ) {
                            ImageWithLoading(
                                imageUrl = state.currentActivity.imageUrl,
                                contentDescription = state.currentActivity.title,
                                height = 180.dp,
                                modifier = Modifier
                                    .widthIn(max = 350.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text("${stringResource(Res.string.starts)}: ${state.currentActivity.startsAt.toDateTimeString()}")
                            Text("${stringResource(Res.string.ends)}: ${state.currentActivity.endsAt?.toDateTimeString() ?: stringResource(Res.string.not_provided)}")
                            listOf<Pair<StringResource, String>>(
                                Pair(Res.string.meet_up_details_title, state.currentActivity.meetUpInformation),
                                Pair(Res.string.location_details_title, state.currentActivity.activityLocation),
                                Pair(Res.string.host_details_title, state.currentActivity.hostInformation),
                                Pair(Res.string.contact_person_details_title, state.currentActivity.contactPersonInformation),
                            ).forEach { (title, text) ->
                                Spacer(Modifier.height(8.dp))
                                DetailsInfoItem(
                                    title = stringResource(title),
                                    text = text.ifBlank { stringResource(Res.string.not_provided) },
                                )
                            }
                        }
                    }
                    1 -> { // Participants Tab
                        if (state.participants.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                var searchQuery by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text(stringResource(Res.string.search)) },
                                    singleLine = true,
                                    trailingIcon = {
                                        AnimatedVisibility(
                                            visible = searchQuery.isNotEmpty(),
                                            enter = expandIn(expandFrom = Alignment.Center),
                                            exit = shrinkOut(shrinkTowards = Alignment.Center)
                                        ) {
                                            IconButton({ searchQuery = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = stringResource(Res.string.empty_search)
                                                )
                                            }
                                        }
                                    },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth()
                                )
                                LazyColumn(
                                    verticalArrangement = spacedBy(4.dp),
                                    modifier = Modifier
                                        .pointerInput(Unit) { detectTapGestures(onTap = { focusManger.clearFocus() }) }
                                        .fillMaxSize()
                                ) {
                                    items(state.participants
                                        .filter { "${it.firstName} ${it.lastName}".contains(searchQuery, true) }
                                    ) { participant ->
                                        ParticipantListItem(
                                            participant,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    item { Spacer(modifier = Modifier.height(8.dp)) }
                                }
                            }
                        } else {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(Res.string.no_participants_yet),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}