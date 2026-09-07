package de.malteans.leisureactivities.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ActivitiesTable : Table("activities") {
    val id = varchar("id", 36)
    val title = varchar("title", 120)
    val startsAt = timestamp("starts_at")
    val endsAt = timestamp("ends_at").nullable()
    val meetUpInformation = text("meet_up_information")
    val activityLocation = text("activity_location")
    val hostInformation = text("host_information")
    val contactPersonInformation = text("contact_person_information")
    val imageId = reference(
        name = "image_id",
        refColumn = ImagesTable.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "activities_image_id"
    ).nullable()
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}