package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.CreateActivityReq
import de.malteans.sosactivities.db.ActivitiesTable
import de.malteans.sosactivities.models.Activity
import de.malteans.sosactivities.services.ActivityService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class ActivityServiceImpl(
    private val db: Database
) : ActivityService {
    override fun list(from: String?, to: String?): List<Activity> = transaction(db) {
        val q = ActivitiesTable.selectAll()
        val condFrom = from?.let { ActivitiesTable.startsAt greaterEq Instant.parse(it) }
        val condTo   = to?.let   { ActivitiesTable.startsAt lessEq Instant.parse(it) }
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
            it[description] = req.description
            it[startsAt] = Instant.parse(req.startsAt)
            it[durationMin] = req.durationMin
            it[imageId] = req.imageId
            it[createdAt] = Instant.now()
            it[updatedAt] = Instant.now()
        }
        ActivitiesTable.selectAll().where { ActivitiesTable.id eq id }.single().toActivity()
    }

    override fun patch(id: String, body: Map<String, Any?>): Activity? = transaction(db) {
        val rows = ActivitiesTable.update({ ActivitiesTable.id eq id }) {
            body["title"]?.let { v -> it[title] = v as String }
            if (body.containsKey("description")) it[description] = body["description"] as String?
            body["startsAt"]?.let { v -> it[startsAt] = Instant.parse(v as String) }
            body["durationMin"]?.let { v -> it[durationMin] = (v as Number).toInt() }
            if (body.containsKey("imageId")) it[imageId] = body["imageId"] as String?
            it[updatedAt] = Instant.now()
        }
        if (rows == 0) return@transaction null
        ActivitiesTable.selectAll().where { ActivitiesTable.id eq id }.single().toActivity()
    }

    override fun setImage(id: String, imageId: String?): Activity? = patch(id, mapOf("imageId" to imageId))

    override fun delete(id: String): Int = transaction(db) {
        ActivitiesTable.deleteWhere { ActivitiesTable.id eq id }
    }

    private fun ResultRow.toActivity() = Activity(
        id = this[ActivitiesTable.id],
        title = this[ActivitiesTable.title],
        description = this[ActivitiesTable.description],
        startsAt = this[ActivitiesTable.startsAt],
        durationMin = this[ActivitiesTable.durationMin],
        imageId = this[ActivitiesTable.imageId]
    )
}
