package de.malteans.leisureactivities.core.presentation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(FormatStringsInDatetimeFormats::class)
object DateTimeFormats {
    val dateTime = LocalDateTime
        .Format { byUnicodePattern("dd.MM.yyyy HH:mm") }
    val date = LocalDateTime
        .Format { byUnicodePattern("dd.MM.yyyy") }
    val time = LocalDateTime
        .Format { byUnicodePattern("HH:mm") }
}

@OptIn(ExperimentalTime::class)
fun Instant.toDateTimeString(): String =
    this.toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DateTimeFormats.dateTime)

@OptIn(ExperimentalTime::class)
fun Instant.toDateString(): String =
    this.toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DateTimeFormats.date)

@OptIn(ExperimentalTime::class)
fun Instant.toTimeString(): String =
    this.toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DateTimeFormats.time)