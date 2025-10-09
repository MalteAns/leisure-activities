package de.malteans.sosactivities.db

import de.malteans.sosactivities.model.RegStatus
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SignUpTable : Table("signups") {
    val id = long("id").autoIncrement()
    val activityId = reference(
        name = "activity_id",
        refColumn = ActivitiesTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "registrations_activity_id"
    )
    val userId = reference(
        name = "user_id",
        refColumn = UsersTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "registrations_user_id"
    )
    val status = enumerationByName("status", 16, RegStatus::class)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
    init {
        index(true, activityId, userId) // matches unique(session_id,user_id)
    }
}