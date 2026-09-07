package de.malteans.leisureactivities.staff.presentation.modifyActivity.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.malteans.leisureactivities.core.presentation.util.toDateString
import kotlinx.datetime.*
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DateTimePickerDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (LocalDateTime) -> Unit,
    initialValue: LocalDateTime? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialValue?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
            ?: Clock.System.now().toEpochMilliseconds(),
    )
    val timePickerState = rememberTimePickerState(
        initialValue?.time?.hour ?: 12,
        initialValue?.time?.minute ?: 0,
    )

    val pagerState = rememberPagerState { 2 }
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedTabIndex = page
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .widthIn(max = 400.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 4.dp)
                        )
                        Text(
                            text = datePickerState.selectedDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toDateString()
                            } ?: stringResource(Res.string.date),
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                        )
                    }
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 4.dp)
                        )
                        Text(
                            text = "${timePickerState.hour}:${timePickerState.minute.toString().padStart(2, '0')}",
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> {
                            DatePicker(
                                state = datePickerState,
                            )
                        }
                        1 -> {
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                TimePicker(
                                    state = timePickerState,
                                    layoutType = TimePickerLayoutType.Vertical,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            if (selectedTabIndex == 0) {
                                selectedTabIndex = 1
                            } else {
                                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                    val selectedDateTime = LocalDateTime(
                                        Instant.fromEpochMilliseconds(selectedDateMillis)
                                            .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                                        LocalTime(
                                            timePickerState.hour,
                                            timePickerState.minute,
                                        )
                                    )
                                    onSubmit(selectedDateTime)
                                }
                            }
                        },
                        enabled = selectedTabIndex == 0 || datePickerState.selectedDateMillis != null
                    ) {
                        Text(stringResource(
                            if (selectedTabIndex == 0) Res.string.next else Res.string.ok
                        ))
                    }
                }
            }
        }
    }
}
