package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.PresignReq
import de.malteans.sosactivities.db.ImagesTable
import de.malteans.sosactivities.models.ImageMeta
import de.malteans.sosactivities.models.Presign
import de.malteans.sosactivities.services.ImageService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class ImageServiceImpl(
    private val db: Database
) : ImageService {
    override fun presignUpload(req: PresignReq): Presign = transaction(db) {
        val id = UUID.randomUUID().toString()
        val key = "uploads/${Instant.now().toString().substring(0,10)}/$id/${req.filename}"
        val fakePutUrl = "https://example.invalid/$key?signature=fake" // replace with real presign later

        ImagesTable.insert {
            it[ImagesTable.id] = id
            it[ownerUserId] = null
            it[bucketKey] = key
            it[publicUrl] = null
            it[mimeType] = req.mimeType
            it[byteSize] = req.size
            it[width] = null
            it[height] = null
            it[sha256Hex] = null
            it[createdAt] = Instant.now()
        }

        Presign(id = id, putUrl = fakePutUrl, key = key)
    }

    override fun finalize(id: String): ImageMeta = transaction(db) {
        val row = ImagesTable.selectAll().where { ImagesTable.id eq id }.single()
        val key = row[ImagesTable.bucketKey]
        val public = "https://cdn.invalid/$key" // compute your CDN/public URL here

        ImagesTable.update({ ImagesTable.id eq id }) {
            it[publicUrl] = public
        }

        ImageMeta(
            id = id,
            publicUrl = public,
            width = row[ImagesTable.width],
            height = row[ImagesTable.height]
        )
    }

    override fun list(mine: Boolean): List<Map<String, Any?>> = transaction(db) {
        ImagesTable
            .selectAll()
            .orderBy(ImagesTable.createdAt, SortOrder.DESC)
            .map { row ->
                mapOf(
                    "id" to row[ImagesTable.id],
                    "publicUrl" to row[ImagesTable.publicUrl],
                    "key" to row[ImagesTable.bucketKey],
                    "mimeType" to row[ImagesTable.mimeType],
                    "size" to row[ImagesTable.byteSize]
                )
            }
    }

    override fun delete(id: String) = transaction(db) {
        ImagesTable.deleteWhere { ImagesTable.id eq id }
        // also remove from storage in your S3/R2/local adapter
    }
}
