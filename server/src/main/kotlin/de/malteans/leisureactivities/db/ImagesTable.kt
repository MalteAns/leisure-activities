package de.malteans.leisureactivities.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ImagesTable : Table("images") {
    val id          = varchar("id", 36)
    val bucketKey   = varchar("bucket_key", 512)
    val publicUrl   = varchar("public_url", 1024).nullable()
    val mimeType    = varchar("mime_type", 100)
    val byteSize    = integer("byte_size")
    val width       = integer("width").nullable()
    val height      = integer("height").nullable()
    val sha256Hex   = varchar("sha256_hex", 64).nullable()
    val createdAt   = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}