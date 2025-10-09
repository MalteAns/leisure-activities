package de.malteans.sosactivities.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : Table("users") {
    val id        = varchar("id", 36)
    val firstName = varchar("first_name", 80)
    val lastName  = varchar("last_name", 80)
    val isStaff   = bool("is_staff").default(false)
    val isAdmin   = bool("is_admin").default(false)
    val updatedAt = timestamp("updated_at")
    val registrationTokenId = reference(
        name = "registration_token_id",
        refColumn = RegistrationTokensTable.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "users_registration_token_id",
    )
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}
