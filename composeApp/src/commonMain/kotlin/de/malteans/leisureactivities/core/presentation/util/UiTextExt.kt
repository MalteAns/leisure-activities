package de.malteans.leisureactivities.core.presentation.util

import de.malteans.leisureactivities.core.data.network.HttpStatusException
import de.malteans.leisureactivities.model.ActivityWithImageUrl
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import kotlin.time.ExperimentalTime

fun Throwable.toUiText() = when (this) {
    is HttpStatusException -> when (this.statusCode.value) {
        502 -> UiText.Resource(Res.string.server_not_started_desc)
        in 500..599 -> UiText.Resource(Res.string.server_error_desc, arrayOf(this.statusCode.value))
        else -> UiText.Resource(Res.string.unexpected_error_desc, arrayOf(this.statusCode.value))
    }
    else -> UiText.DynamicString(this.message ?: this::class.simpleName ?: "Unknown error")
}

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
fun ActivityWithImageUrl.toUiTexts(): List<UiText> {
    val result = mutableListOf<UiText>(
        if (this.endsAt == null) UiText.Resource(
            Res.string.activity_at_information_tts,
            arrayOf(this.title, this.startsAt.toDateString(), this.startsAt.toTimeString())
        ) else UiText.Resource(
            Res.string.activity_information_tts,
            arrayOf(this.title, this.startsAt.toDateString(), this.startsAt.toTimeString(), this.endsAt!!.toTimeString())
        )
    )
    listOf<Pair<StringResource, String>>(
        Pair(Res.string.activity_meet_up_information_tts, this.meetUpInformation),
        Pair(Res.string.activity_location_information_tts, this.activityLocation),
        Pair(Res.string.activity_who_information_tts, this.hostInformation),
        Pair(Res.string.activity_contact_person_information_tts, this.contactPersonInformation)
    ).forEach { (resId, value) ->
        if (value.isNotBlank()) {
            result.add(UiText.Resource(resId, arrayOf(value)))
        }
    }
    return result
}