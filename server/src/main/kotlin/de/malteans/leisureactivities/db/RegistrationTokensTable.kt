package de.malteans.leisureactivities.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object RegistrationTokensTable: Table("registration_tokens") {
    val id        = integer("id").autoIncrement()
    val token     = varchar("token", 128)
    val comment   = varchar("comment", 256).nullable()
    val expired   = bool("expired")
    val createdAt = timestamp("created_at").default(Instant.now())
    override val primaryKey = PrimaryKey(id)
}