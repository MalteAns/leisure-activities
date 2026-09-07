@file:OptIn(ExperimentalTime::class)

package de.malteans.leisureactivities.model.ext

import de.malteans.leisureactivities.dto.ActivityDto
import de.malteans.leisureactivities.model.Activity
import de.malteans.leisureactivities.model.ActivityWithImageUrl
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Activity.toDto(
    imageUrl: String?
) = ActivityDto(
    id = this.id,
    title = this.title,
    startsAt = this.startsAt.toString(),
    endsAt = this.endsAt?.toString(),
    meetUpInformation = this.meetUpInformation,
    activityLocation = this.activityLocation,
    hostInformation = this.hostInformation,
    contactPersonInformation = this.contactPersonInformation,
    imageId = this.imageId,
    imageUrl = imageUrl,
)

fun ActivityDto.toDomain() = Activity(
    id = this.id,
    title = this.title,
    startsAt = Instant.parse(this.startsAt),
    endsAt = this.endsAt?.let { endsAt -> Instant.parse(endsAt) },
    meetUpInformation = this.meetUpInformation,
    activityLocation = this.activityLocation,
    hostInformation = this.hostInformation,
    contactPersonInformation = this.contactPersonInformation,
    imageId = this.imageId,
)

/** @param [serverBaseUrl] The servers url without a trailing `/`. e.g. `https://example.de` */
fun ActivityDto.toDomainWithImageUrl(
    serverBaseUrl: String,
) = ActivityWithImageUrl(
    id = this.id,
    title = this.title,
    startsAt = Instant.parse(this.startsAt),
    endsAt = this.endsAt?.let { endsAt -> Instant.parse(endsAt) },
    meetUpInformation = this.meetUpInformation,
    activityLocation = this.activityLocation,
    hostInformation = this.hostInformation,
    contactPersonInformation = this.contactPersonInformation,
    imageId = this.imageId,
    imageUrl = this.imageUrl?.let { imageUrl -> "$serverBaseUrl$imageUrl" },
    signedUp = null,
)