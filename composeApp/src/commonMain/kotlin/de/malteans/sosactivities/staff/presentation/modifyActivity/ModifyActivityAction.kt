package de.malteans.sosactivities.staff.presentation.modifyActivity

import de.malteans.sosactivities.model.Image
import de.malteans.sosactivities.staff.presentation.modifyActivity.components.PickedImageData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
sealed interface ModifyActivityAction {
    data object NavigateBack : ModifyActivityAction

    data class OnTitleChange(val newValue: String) : ModifyActivityAction
    data class OnImageChange(val newImage: Image?) : ModifyActivityAction
    data class OnStartsAtChange(val newValue: Instant?) : ModifyActivityAction
    data class OnEndsAtChange(val newValue: Instant?) : ModifyActivityAction
    data class OnMeetUpInformationChange(val newValue: String) : ModifyActivityAction
    data class OnActivityLocationChange(val newValue: String) : ModifyActivityAction
    data class OnHostInformationChange(val newValue: String) : ModifyActivityAction
    data class OnContactPersonInformationChange(val newValue: String) : ModifyActivityAction

    data class SetShowImage(val newValue: Boolean) : ModifyActivityAction
    data class OnUploadImage(val imageUploadData: PickedImageData) : ModifyActivityAction

    data object SaveChanges : ModifyActivityAction
}