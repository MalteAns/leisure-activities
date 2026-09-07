package de.malteans.leisureactivities.services.impl

import de.malteans.leisureactivities.db.SignUpTable
import de.malteans.leisureactivities.db.UsersTable
import de.malteans.leisureactivities.model.User
import de.malteans.leisureactivities.services.UserService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserServiceImpl(
    private val db: Database
) : UserService {
    override fun insert(id: String, firstName: String, lastName: String, tokenId: Int): Result<User> = transaction(db) {
        val exists = UsersTable.selectAll().where { UsersTable.id eq id }.limit(1).any()
        if (exists) {
            return@transaction Result.failure(IllegalArgumentException("UserId $id already in use."))
        }
        UsersTable.insert {
            it[UsersTable.id] = id
            it[this.firstName] = firstName
            it[this.lastName] = lastName
            it[UsersTable.isStaff] = isStaff
            it[updatedAt] = Instant.now()
            it[createdAt] = Instant.now()
            it[registrationTokenId] = tokenId
        }
        val row = UsersTable.selectAll().where { UsersTable.id eq id }.single()
        Result.success(row.toUser())
    }

    override fun get(id: String): User = transaction(db) {
        UsersTable.selectAll().where { UsersTable.id eq id }.single().toUser()
    }

    override fun updateName(id: String, firstName: String, lastName: String): User = transaction(db) {
        UsersTable.update({ UsersTable.id eq id }) {
            it[this.firstName] = firstName
            it[this.lastName] = lastName
            it[this.updatedAt] = Instant.now()
        }
        UsersTable.selectAll().where { UsersTable.id eq id }.single().toUser()
    }

    override fun updateIsStaff(id: String, value: Boolean): User = transaction(db) {
        UsersTable.update({ UsersTable.id eq id }) {
            it[isStaff] = value
            it[updatedAt] = Instant.now()
        }
        UsersTable.selectAll().where { UsersTable.id eq id }.single().toUser()
    }

    override fun getSignUps(id: String): Set<String> = transaction(db) {
        SignUpTable
            .select(SignUpTable.activityId)
            .where {
                SignUpTable.userId eq id
            }
            .map {
                it[SignUpTable.activityId]
            }
            .toSet()
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id],
        firstName = this[UsersTable.firstName],
        lastName = this[UsersTable.lastName],
        isStaff = this[UsersTable.isStaff],
        isAdmin = this[UsersTable.isAdmin],
    )
}
