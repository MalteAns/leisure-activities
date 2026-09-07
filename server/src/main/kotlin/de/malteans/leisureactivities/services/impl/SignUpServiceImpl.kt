package de.malteans.leisureactivities.services.impl

import de.malteans.leisureactivities.db.SignUpTable
import de.malteans.leisureactivities.db.UsersTable
import de.malteans.leisureactivities.model.Participant
import de.malteans.leisureactivities.model.RegStatus
import de.malteans.leisureactivities.model.Roster
import de.malteans.leisureactivities.services.SignUpService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class SignUpServiceImpl(
    private val db: Database
) : SignUpService {
    override fun register(activityId: String, userId: String): RegStatus = transaction(db) {
        // Rely on unique(activity_id,user_id) and handle duplicate as idempotent
        val already = SignUpTable
            .selectAll()
            .where { (SignUpTable.activityId eq activityId) and (SignUpTable.userId eq userId) }
            .limit(1).singleOrNull()

        if (already != null) return@transaction already[SignUpTable.status]

        SignUpTable.insert {
            it[SignUpTable.activityId] = activityId
            it[SignUpTable.userId] = userId
            it[status] = RegStatus.CONFIRMED // no capacity logic yet
            it[createdAt] = Instant.now()
        }

        RegStatus.CONFIRMED
    }

    override fun cancel(activityId: String, userId: String): Int = transaction(db) {
        SignUpTable.deleteWhere {
            (SignUpTable.activityId eq activityId) and (SignUpTable.userId eq userId)
        }
        // If you later add waitlist, promote next here (inside same transaction)
    }

    override fun roster(activityId: String): Roster = transaction(db) {
        val q = SignUpTable
            .join(UsersTable, JoinType.INNER, additionalConstraint = {
                SignUpTable.userId eq UsersTable.id
            })
            .select(UsersTable.id, UsersTable.firstName, UsersTable.lastName, SignUpTable.status)
            .where { SignUpTable.activityId eq activityId }

        val confirmed = mutableListOf<Participant>()
        val waitlist  = mutableListOf<Participant>()

        q.forEach { row ->
            val item = Participant(row[UsersTable.id], row[UsersTable.firstName], row[UsersTable.lastName])
            when (row[SignUpTable.status]) {
                RegStatus.CONFIRMED -> confirmed += item
                RegStatus.WAITLISTED -> waitlist  += item
                RegStatus.CANCELLED  -> { /* ignore cancelled in roster */ }
            }
        }
        Roster(confirmed = confirmed, waitlist = waitlist)
    }
}
