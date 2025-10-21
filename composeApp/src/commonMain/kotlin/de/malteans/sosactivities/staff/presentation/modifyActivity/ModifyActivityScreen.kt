package de.malteans.sosactivities.staff.presentation.modifyActivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.components.CustomTopBar
import de.malteans.sosactivities.core.presentation.components.ImageWithLoading
import de.malteans.sosactivities.core.presentation.components.LoadingOverlayBox
import de.malteans.sosactivities.core.presentation.util.toDateTimeString
import de.malteans.sosactivities.core.presentation.util.toTimeString
import de.malteans.sosactivities.staff.presentation.modifyActivity.components.DateTimePickerDialog
import de.malteans.sosactivities.staff.presentation.modifyActivity.components.ImagePickerDialog
import de.malteans.sosactivities.staff.presentation.modifyActivity.components.ModifyActivityTextField
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun ModifyActivityScreenRoot(
    viewModel: ModifyActivityViewModel = koinViewModel(),
    activityId: String?,
    navigateBack: () -> Unit,
) {
    LaunchedEffect(activityId) {
        viewModel.setActivityId(activityId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ModifyActivityScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ModifyActivityAction.NavigateBack -> navigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalTime::class, ExperimentalComposeUiApi::class)
@Composable
fun ModifyActivityScreen(
    state: ModifyActivityState,
    onAction: (ModifyActivityAction) -> Unit
) {
    val focusManger = LocalFocusManager.current

    val unsavedChanges by remember(state.newTitle, state.newStartsAt, state.newEndsAt, state.newImage, state.newMeetUpInformation, state.newActivityLocation, state.newHostInformation, state.newContactPersonInformation) {
        mutableStateOf(
            state.newTitle.isNotNullOrBlank() ||
            state.newStartsAt != null ||
            state.newEndsAt != null ||
            state.newImage != null ||
            state.newMeetUpInformation != null ||
            state.newActivityLocation != null ||
            state.newHostInformation != null ||
            state.newContactPersonInformation != null
        )
    }

    val validToSave by remember(state.newTitle, state.newStartsAt, state.newEndsAt, state.loadedActivity, unsavedChanges) {
        mutableStateOf(
            (
                (state.newTitle.isNotNullOrBlank() && state.newStartsAt != null) ||
                (state.loadedActivity != null && unsavedChanges)
            )
            && (state.newStartsAt == null || (state.newEndsAt ?: state.loadedActivity?.endsAt)?.let { it > state.newStartsAt } != false)
        )
    }

    var showImagePicker by remember { mutableStateOf(false) }
    // See below scaffold for implementation

    var showDateTimePicker by remember { mutableStateOf(false) }
    var datePickerInitialValue by remember { mutableStateOf<Instant?>(null) }
    var onDatePickerResult by remember { mutableStateOf<((Instant) -> Unit)?>(null) }
    if (showDateTimePicker) {
        DateTimePickerDialog(
            onDismissRequest = {
                showDateTimePicker = false
                datePickerInitialValue = null
                onDatePickerResult = null
            },
            onSubmit = { localDateTime ->
                onDatePickerResult?.invoke(localDateTime.toInstant(TimeZone.currentSystemDefault()))
                showDateTimePicker = false
                datePickerInitialValue = null
                onDatePickerResult = null
            },
            initialValue = datePickerInitialValue?.toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text(stringResource(Res.string.unsaved_changes)) },
            text = { Text(stringResource(Res.string.unsaved_changes_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUnsavedChangesDialog = false
                        onAction(ModifyActivityAction.NavigateBack)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(Res.string.discard_changes))
                }
            },
            dismissButton = {
                TextButton({ showUnsavedChangesDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    val onBack = {
        if (unsavedChanges) {
            showUnsavedChangesDialog = true
        } else {
            onAction(ModifyActivityAction.NavigateBack)
        }
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = when {
                    state.loadingActivity -> ""
                    state.loadedActivity != null -> state.loadedActivity.title
                    else -> stringResource(Res.string.create_activity)
                },
                subtitle = (state.newStartsAt ?: state.loadedActivity?.startsAt)?.toDateTimeString()
                    ?.plus((state.newEndsAt ?: state.loadedActivity?.endsAt)?.let { " - ${it.toTimeString()}" } ?: ""),
                navigationIcon = {
                    IconButton(onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            focusManger.clearFocus()
                            onAction(ModifyActivityAction.SaveChanges)
                        },
                        enabled = validToSave && !state.savingInProcess,
                    ) {
                        AnimatedVisibility(
                            state.savingInProcess
                        ) {

                        }
                        if (!state.savingInProcess) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(Res.string.save)
                            )
                        } else {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(18.dp),
                            )
                        }
                    }
                },
                modifier = Modifier
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManger.clearFocus() }) }
            )
        },
    ) { paddingValues ->
        if (state.loadingActivity) return@Scaffold LoadingOverlayBox(Modifier.padding(paddingValues))
        Column(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManger.clearFocus()
                    })
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            ModifyActivityTextField(
                newValue = state.newTitle,
                loadedValue = state.loadedActivity?.title,
                onValueChange = { newValue -> onAction(ModifyActivityAction.OnTitleChange(newValue)) },
                labelRes = Res.string.title,
                isError = (state.newTitle ?: state.loadedActivity?.title).isNullOrBlank(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions {
                    focusManger.clearFocus()
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ModifyActivityTextField(
                    newValue = state.newImage?.filename,
                    loadedValue = state.loadedActivityImage?.filename,
                    onValueChange = null,
                    labelRes = Res.string.image,
                    trailingContent = {
                        IconButton({ showImagePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = stringResource(Res.string.select_image)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = { onAction(ModifyActivityAction.SetShowImage(!state.showImage)) },
                    enabled = (state.newImage ?: state.loadedActivityImage) != null,
                    modifier = Modifier.padding(top = 8.dp) // Center with OutlinedTextField
                ) {
                    Icon(
                        imageVector = if (state.showImage) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = stringResource(
                            if (state.showImage) Res.string.hide_image else Res.string.show_image
                        ),
                        tint = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if ((state.newImage ?: state.loadedActivityImage) != null) 1f else 0.4f,
                        ),
                    )
                }
            }
            AnimatedVisibility(
                visible = state.showImage,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                ImageWithLoading(
                    imageUrl = state.newImage?.publicUrl ?: state.loadedActivityImage?.publicUrl,
                    contentDescription = stringResource(Res.string.image),
                    height = 180.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .widthIn(max = 350.dp)
                )
            }
            ModifyActivityTextField(
                newValue = state.newStartsAt?.toDateTimeString(),
                loadedValue = state.loadedActivity?.startsAt?.toDateTimeString(),
                onValueChange = null,
                labelRes = Res.string.starts,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = stringResource(Res.string.edit_start)
                    )
                },
                isError = (state.newStartsAt ?: state.loadedActivity?.startsAt) == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        datePickerInitialValue = state.newStartsAt ?: state.loadedActivity?.startsAt
                        onDatePickerResult = { instant ->
                            onAction(ModifyActivityAction.OnStartsAtChange(instant))
                        }
                        showDateTimePicker = true
                    }
            )
            ModifyActivityTextField(
                newValue = state.newEndsAt?.toDateTimeString(),
                loadedValue = state.loadedActivity?.endsAt?.toDateTimeString(),
                onValueChange = null,
                labelRes = Res.string.ends,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = stringResource(Res.string.edit_end)
                    )
                },
                isError = (state.newStartsAt ?: state.loadedActivity?.startsAt)?.let { startsAt ->
                    (state.newEndsAt ?: state.loadedActivity?.endsAt)?.let { endsAt ->
                        startsAt > endsAt
                    }
                } == true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        datePickerInitialValue = state.newEndsAt ?: state.loadedActivity?.endsAt
                        onDatePickerResult = { instant ->
                            onAction(ModifyActivityAction.OnEndsAtChange(instant))
                        }
                        showDateTimePicker = true
                    }
            )
            ModifyActivityTextField(
                newValue = state.newMeetUpInformation,
                loadedValue = state.loadedActivity?.meetUpInformation,
                onValueChange = { newValue -> onAction(ModifyActivityAction.OnMeetUpInformationChange(newValue)) },
                labelRes = Res.string.meet_up_information,
                lines = 3 to 5,
            )
            ModifyActivityTextField(
                newValue = state.newActivityLocation,
                loadedValue = state.loadedActivity?.activityLocation,
                onValueChange = { newValue -> onAction(ModifyActivityAction.OnActivityLocationChange(newValue)) },
                labelRes = Res.string.activity_location,
                lines = 3 to 5,
            )
            ModifyActivityTextField(
                newValue = state.newHostInformation,
                loadedValue = state.loadedActivity?.hostInformation,
                onValueChange = { newValue -> onAction(ModifyActivityAction.OnHostInformationChange(newValue)) },
                labelRes = Res.string.host_information,
                lines = 3 to 5,
            )
            ModifyActivityTextField(
                newValue = state.newContactPersonInformation,
                loadedValue = state.loadedActivity?.contactPersonInformation,
                onValueChange = { newValue -> onAction(ModifyActivityAction.OnContactPersonInformationChange(newValue)) },
                labelRes = Res.string.contact_person_information,
                lines = 1 to 3,
            )
            Spacer(Modifier.height(128.dp))
        }
        if (state.savingInProcess) LoadingOverlayBox(Modifier.padding(paddingValues))
    }


    if (showImagePicker) {
        ImagePickerDialog(
            allImages = state.allImages,
            onUploadImage = { data -> onAction(ModifyActivityAction.OnUploadImage(data)) },
            uploadInProgress = state.imageUploadInProgress,
            onDismissRequest = { showImagePicker = false },
            onImagePicked = { image -> onAction(ModifyActivityAction.OnImageChange(image)) }
        )
    }
}

fun String?.isNotNullOrBlank() = this?.isNotBlank() == true