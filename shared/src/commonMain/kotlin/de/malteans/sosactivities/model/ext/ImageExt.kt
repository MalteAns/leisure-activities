package de.malteans.sosactivities.model.ext

import de.malteans.sosactivities.Constants
import de.malteans.sosactivities.dto.ImageDto
import de.malteans.sosactivities.model.Image

fun ImageDto.toDomain() = Image(
    id = this.id,
    filename = this.filename,
    publicUrl = Constants.BASE_URL + this.publicUrl,
    mimeType = this.mimeType,
    byteSize = this.byteSize,
    width = this.width,
    height = this.height,
)