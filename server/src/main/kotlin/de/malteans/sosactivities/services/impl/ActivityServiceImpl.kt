package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.db.ActivitiesTable
import de.malteans.sosactivities.dto.CreateActivityReq
import de.malteans.sosactivities.dto.UpdateActivityReq
import de.malteans.sosactivities.model.Activity
import de.malteans.sosactivities.services.ActivityService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

class ActivityServiceImpl(
    private val db: Database
) : ActivityService {
    override fun list(from: String?, to: String?): List<Activity> = transaction(db) {
        val q = ActivitiesTable.selectAll()
        val condFrom = from?.let { ActivitiesTable.startsAt greaterEq Instant.parse(it) }
        val condTo = to?.let { ActivitiesTable.startsAt lessEq Instant.parse(it) }
        val where = when {
            condFrom != null && condTo != null -> condFrom and condTo
            condFrom != null -> condFrom
            condTo   != null -> condTo
            else -> null
        }
        val base = if (where != null) q.where { where } else q
        base.orderBy(ActivitiesTable.startsAt to SortOrder.ASC).map { it.toActivity() }
    }

    override fun get(id: String): Activity? = transaction(db) {
        ActivitiesTable.selectAll().where { ActivitiesTable.id eq id }.singleOrNull()?.toActivity()
    }

    override fun create(req: CreateActivityReq): Activity = transaction(db) {
        val id = UUID.randomUUID().toString()
        ActivitiesTable.insert {
            it[ActivitiesTable.id] = id
            it[title] = req.title
            it[startsAt] = Instant.parse(req.startsAt)
            it[endsAt] = req.endsAt?.let { endsAt ->  Instant.parse(endsAt)}
            it[meetUpInformation] = req.meetUpInformation
            it[activityLocation] = req.activityLocation
            it[hostInformation] = req.hostInformation
            it[contactPersonInformation] = req.contactPersonInformation
            it[imageId] = req.imageId
            it[updatedAt] = Instant.now()
            it[createdAt] = Instant.now()
        }
        ActivitiesTable.selectAll().where { ActivitiesTable.id eq id }.single().toActivity()
    }

    override fun update(id: String, updateActivityReq: UpdateActivityReq): Activity? = transaction(db) {
        val updatedRows = ActivitiesTable.update({ ActivitiesTable.id eq id }) {
            it[title] = updateActivityReq.title
            it[startsAt] = Instant.parse(updateActivityReq.startsAt)
            it[endsAt] = updateActivityReq.endsAt?.let { endsAt ->  Instant.parse(endsAt)}
            it[meetUpInformation] = updateActivityReq.meetUpInformation
            it[activityLocation] = updateActivityReq.activityLocation
            it[hostInformation] = updateActivityReq.hostInformation
            it[contactPersonInformation] = updateActivityReq.contactPersonInformation
            it[imageId] = updateActivityReq.imageId
            it[updatedAt] = Instant.now()
        }
        if (updatedRows == 0) return@transaction null
        ActivitiesTable.selectAll().where { ActivitiesTable.id eq id }.single().toActivity()
    }

    override fun delete(id: String): Int = transaction(db) {
        ActivitiesTable.deleteWhere { ActivitiesTable.id eq id }
    }

    @OptIn(ExperimentalTime::class)
    private fun ResultRow.toActivity() = Activity(
        id = this[ActivitiesTable.id],
        title = this[ActivitiesTable.title],
        startsAt = this[ActivitiesTable.startsAt].toKotlinInstant(),
        endsAt = this[ActivitiesTable.endsAt]?.toKotlinInstant(),
        meetUpInformation = this[ActivitiesTable.meetUpInformation],
        activityLocation = this[ActivitiesTable.activityLocation],
        hostInformation = this[ActivitiesTable.hostInformation],
        contactPersonInformation = this[ActivitiesTable.contactPersonInformation],
        imageId = this[ActivitiesTable.imageId],
    )
}
