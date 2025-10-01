package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.db.RegStatus
import de.malteans.sosactivities.db.RegistrationsTable
import de.malteans.sosactivities.db.UsersTable
import de.malteans.sosactivities.db.displayName
import de.malteans.sosactivities.models.RegItem
import de.malteans.sosactivities.models.Roster
import de.malteans.sosactivities.services.RegistrationService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class RegistrationServiceImpl(
    private val db: Database
) : RegistrationService {
    override fun register(activityId: String, userId: String): String = transaction(db) {
        // Rely on unique(activity_id,user_id) and handle duplicate as idempotent
        val already = RegistrationsTable
            .selectAll()
            .where { (RegistrationsTable.activityId eq activityId) and (RegistrationsTable.userId eq userId) }
            .limit(1).singleOrNull()

        if (already != null) return@transaction already[RegistrationsTable.status].name

        RegistrationsTable.insert {
            it[RegistrationsTable.activityId] = activityId
            it[RegistrationsTable.userId] = userId
            it[status] = RegStatus.CONFIRMED // no capacity logic yet
            it[createdAt] = Instant.now()
        }

        RegStatus.CONFIRMED.name
    }

    override fun cancel(activityId: String, userId: String): Int = transaction(db) {
        RegistrationsTable.deleteWhere {
            (RegistrationsTable.activityId eq activityId) and (RegistrationsTable.userId eq userId)
        }
        // If you later add waitlist, promote next here (inside same transaction)
    }

    override fun roster(activityId: String): Roster = transaction(db) {
        val q = RegistrationsTable
            .join(UsersTable, JoinType.INNER, additionalConstraint = {
                RegistrationsTable.userId eq UsersTable.id
            })
            .select(UsersTable.id, UsersTable.displayName, RegistrationsTable.status)
            .where { RegistrationsTable.activityId eq activityId }

        val confirmed = mutableListOf<RegItem>()
        val waitlist  = mutableListOf<RegItem>()

        q.forEach { row ->
            val item = RegItem(row[UsersTable.id], row[UsersTable.displayName])
            when (row[RegistrationsTable.status]) {
                RegStatus.CONFIRMED -> confirmed += item
                RegStatus.WAITLISTED -> waitlist  += item
                RegStatus.CANCELLED  -> { /* ignore cancelled in roster */ }
            }
        }
        Roster(confirmed = confirmed, waitlist = waitlist)
    }
}
