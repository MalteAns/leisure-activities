package de.malteans.sosactivities.db

import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.stringLiteral

enum class RegStatus { CONFIRMED, WAITLISTED, CANCELLED }

object UsersTable : Table("users") {
    val id        = varchar("id", 36)
    val firstName = varchar("first_name", 80)
    val lastName  = varchar("last_name", 80)
    val isStaff   = bool("is_staff").default(false)
    val isAdmin   = bool("is_admin").default(false)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

val UsersTable.displayName
    get() = concat(
        UsersTable.firstName,
        stringLiteral(" "),
        UsersTable.lastName
    )

object ActivitiesTable : Table("activities") {
    val id          = varchar("id", 36)
    val title       = varchar("title", 120)
    val description = text("description").nullable()
    val startsAt    = timestamp("starts_at")
    val durationMin = integer("duration_min") // db has SMALLINT UNSIGNED; integer is fine in Exposed
    val imageUrl    = varchar("image_url", 512).nullable()
    val createdAt   = timestamp("created_at")
    val updatedAt   = timestamp("updated_at")
    val imageId     = varchar("image_id", 36).nullable()
    override val primaryKey = PrimaryKey(id)
}

object RegistrationsTable : Table("registrations") {
    val id         = long("id").autoIncrement()
    val activityId = varchar("activity_id", 36).index()
    val userId     = varchar("user_id", 36).index()
    val status     = enumerationByName("status", 16, RegStatus::class)
    val createdAt  = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
    init {
        index(true, activityId, userId) // matches unique(session_id,user_id)
    }
}

object ImagesTable : Table("images") {
    val id          = varchar("id", 36)
    val ownerUserId = varchar("owner_user_id", 36).nullable()
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
