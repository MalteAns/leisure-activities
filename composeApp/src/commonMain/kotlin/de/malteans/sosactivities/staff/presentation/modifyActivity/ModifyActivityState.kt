package de.malteans.sosactivities.staff.presentation.modifyActivity

import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.model.Image
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class ModifyActivityState(
    val loadingActivity: Boolean = true,
    val loadingActivityError: Throwable? = null,

    val savingInProcess: Boolean = false,
    val savingError: Throwable? = null,

    val allImages: List<Image> = emptyList(),

    val loadedActivity: ActivityWithImageUrl? = null,
    val loadedActivityImage: Image? = null,

    val newTitle: String? = null,
    val newImage: Image? = null,
    val newStartsAt: Instant? = null,
    val newEndsAt: Instant? = null,
    val newMeetUpInformation: String? = null,
    val newActivityLocation: String? = null,
    val newHostInformation: String? = null,
    val newContactPersonInformation: String? = null,

    val removeImage: Boolean = false, // TODO: Implement removing the image

    val showImage: Boolean = false,

    val imageUploadInProgress: Boolean = false,
    val imageUploadError: Throwable? = null,
)
