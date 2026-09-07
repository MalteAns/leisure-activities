package de.malteans.leisureactivities.services

import de.malteans.leisureactivities.dto.ImageDto
import de.malteans.leisureactivities.dto.ImagePresignReq
import de.malteans.leisureactivities.model.ImageMeta
import de.malteans.leisureactivities.model.Presign

interface ImageService {
    fun presignUpload(req: ImagePresignReq): Presign
    fun saveFile(id: String, bytes: ByteArray)
    fun finalize(id: String): ImageMeta
    fun getUrl(id: String): String?
    fun list(): List<ImageDto> // replace with ImageDto
    fun delete(id: String): Int
}