package de.malteans.sosactivities.services

import de.malteans.sosactivities.PresignReq
import de.malteans.sosactivities.models.ImageMeta
import de.malteans.sosactivities.models.Presign

interface ImageService {
    fun presignUpload(req: PresignReq): Presign
    fun finalize(id: String): ImageMeta
    fun list(mine: Boolean): List<Map<String, Any?>> // replace with ImageDto
    fun delete(id: String): Int
}