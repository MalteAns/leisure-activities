package de.malteans.sosactivities.services

import de.malteans.sosactivities.dto.ImageDto
import de.malteans.sosactivities.dto.ImagePresignReq
import de.malteans.sosactivities.model.ImageMeta
import de.malteans.sosactivities.model.Presign

interface ImageService {
    fun presignUpload(req: ImagePresignReq): Presign
    fun saveFile(id: String, bytes: ByteArray)
    fun finalize(id: String): ImageMeta
    fun getUrl(id: String): String?
    fun list(): List<ImageDto> // replace with ImageDto
    fun delete(id: String): Int
}