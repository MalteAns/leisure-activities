package de.malteans.sosactivities.staff.presentation.modifyActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.core.presentation.util.UiText
import de.malteans.sosactivities.model.Image
import de.malteans.sosactivities.staff.domain.StaffService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sosactivities.composeapp.generated.resources.Res
import sosactivities.composeapp.generated.resources.activity_created
import sosactivities.composeapp.generated.resources.changes_saved
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ModifyActivityViewModel(
    private val staffService: StaffService,
): ViewModel() {

    private val _allImages = MutableStateFlow(emptyList<Image>())
    private val _state = MutableStateFlow(ModifyActivityState())

    val state = combine(
        _state,
        _allImages,
    ) { state, allImages ->
        state.copy(
            allImages = allImages,
            loadedActivityImage = allImages.find { it.id == state.loadedActivity?.imageId }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModifyActivityState()
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            staffService.getAllImages()
                .onSuccess { images ->
                    _allImages.update { images }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }

    fun setActivityId(activityId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(loadingActivity = true) }
            if (activityId == null) {
                _state.update { it.copy(
                    loadedActivity = null,
                    loadingActivity = false,
                ) }
            } else {
                staffService.getActivity(activityId)
                    .onSuccess { activity ->
                        _state.update { it.copy(
                            loadedActivity = activity,
                            loadingActivity = false,
                            loadingActivityError = null,
                        ) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(
                            loadingActivity = false,
                            loadingActivityError = error,
                        ) }
                    }
            }
        }
    }

    fun onAction(action: ModifyActivityAction) {
        when(action) {
            is ModifyActivityAction.OnTitleChange -> _state.update { state ->
                state.copy(newTitle = action.newValue.takeIf { it != state.loadedActivity?.title })
            }
            is ModifyActivityAction.OnImageChange -> _state.update { state ->
                state.copy(newImage = action.newImage.takeIf { it?.id != state.loadedActivity?.imageId })
            }
            is ModifyActivityAction.OnStartsAtChange -> _state.update { state ->
                state.copy(newStartsAt = action.newValue.takeIf { it != state.loadedActivity?.startsAt })
            }
            is ModifyActivityAction.OnEndsAtChange -> _state.update { state ->
                state.copy(newEndsAt = action.newValue.takeIf { it != state.loadedActivity?.endsAt })
            }
            is ModifyActivityAction.OnMeetUpInformationChange -> _state.update { state ->
                state.copy(newMeetUpInformation = action.newValue.takeIf { it != state.loadedActivity?.meetUpInformation })
            }
            is ModifyActivityAction.OnActivityLocationChange -> _state.update { state ->
                state.copy(newActivityLocation = action.newValue.takeIf { it != state.loadedActivity?.activityLocation })
            }
            is ModifyActivityAction.OnHostInformationChange -> _state.update { state ->
                state.copy(newHostInformation = action.newValue.takeIf { it != state.loadedActivity?.hostInformation })
            }
            is ModifyActivityAction.OnContactPersonInformationChange -> _state.update { state ->
                state.copy(newContactPersonInformation = action.newValue.takeIf { it != state.loadedActivity?.contactPersonInformation })
            }
            is ModifyActivityAction.SetShowImage -> _state.update { it.copy(showImage = action.newValue) }
            is ModifyActivityAction.OnUploadImage -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(imageUploadInProgress = true) }
                    staffService.uploadImage(
                        filename = action.imageUploadData.filename,
                        imageBytes = action.imageUploadData.bytes,
                        mimeType = action.imageUploadData.mimeType
                    )
                        .onSuccess { image ->
                            _allImages.update { it + image }
                            _state.update { it.copy(
                                imageUploadInProgress = false,
                                imageUploadError = null,
                            ) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(
                                imageUploadInProgress = false,
                                imageUploadError = error,
                            ) }
                        }
                }
            }
            ModifyActivityAction.SaveChanges -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(savingInProcess = true) }
                    val currentState = state.value

                    (if (currentState.loadedActivity == null) staffService.createActivity(
                            title = currentState.newTitle ?: throw IllegalStateException("Title must be set. And should be checked before calling this."),
                            startsAt = currentState.newStartsAt ?: throw IllegalStateException("StartsAt must be set. And should be checked before calling this."),
                            endsAt = currentState.newEndsAt,
                            meetUpInformation = currentState.newMeetUpInformation ?: "",
                            activityLocation = currentState.newActivityLocation ?: "",
                            hostInformation = currentState.newHostInformation ?: "",
                            contactPersonInformation = currentState.newContactPersonInformation ?: "",
                            imageId = currentState.newImage?.id
                    ) else staffService.updateActivity(
                        activityId = currentState.loadedActivity.id,
                        title = currentState.newTitle ?: currentState.loadedActivity.title,
                        startsAt = currentState.newStartsAt ?: currentState.loadedActivity.startsAt,
                        endsAt = currentState.newEndsAt ?: currentState.loadedActivity.endsAt,
                        meetUpInformation = currentState.newMeetUpInformation ?: currentState.loadedActivity.meetUpInformation,
                        activityLocation = currentState.newActivityLocation ?: currentState.loadedActivity.activityLocation,
                        hostInformation = currentState.newHostInformation ?: currentState.loadedActivity.hostInformation,
                        contactPersonInformation = currentState.newContactPersonInformation ?: currentState.loadedActivity.contactPersonInformation,
                        imageId = (currentState.newImage?.id ?: currentState.loadedActivity.imageId).takeUnless { currentState.removeImage },
                    ))
                        .onSuccess { newActivity ->
                            SnackbarManager.showSnackbar(
                                UiText.Resource(
                                    if (currentState.loadedActivity == null) Res.string.activity_created
                                    else Res.string.changes_saved
                                ),
                                withDismissAction = true
                            )
                            resetNewValues()
                            _state.update { it.copy(
                                loadedActivity = newActivity,
                                savingInProcess = false,
                                savingError = null,
                            ) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(
                                savingInProcess = false,
                                savingError = error,
                            ) }
                        }
                }
            }
            else -> throw NotImplementedError("Action '$action' not implemented in ModifyActivityViewModel")
        }
    }

    private fun resetNewValues() {
        _state.update { state ->
            state.copy(
                newTitle = null,
                newImage = null,
                newStartsAt = null,
                newEndsAt = null,
                newMeetUpInformation = null,
                newActivityLocation = null,
                newHostInformation = null,
                newContactPersonInformation = null,
                removeImage = false,
            )
        }
    }
}