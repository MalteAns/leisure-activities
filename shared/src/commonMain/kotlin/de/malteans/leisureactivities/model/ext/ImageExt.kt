package de.malteans.leisureactivities.model.ext

import de.malteans.leisureactivities.Constants
import de.malteans.leisureactivities.dto.ImageDto
import de.malteans.leisureactivities.model.Image

fun ImageDto.toDomain() = Image(
    id = this.id,
    filename = this.filename,
    publicUrl = Constants.BASE_URL + this.publicUrl,
    mimeType = this.mimeType,
    byteSize = this.byteSize,
    width = this.width,
    height = this.height,
)