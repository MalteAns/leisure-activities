package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.db.UsersTable
import de.malteans.sosactivities.models.User
import de.malteans.sosactivities.services.UserService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UserServiceImpl(
    private val db: Database
) : UserService {
    override fun insert(id: String, firstName: String, lastName: String): Result<User> = transaction(db) {
        val exists = UsersTable.selectAll().where { UsersTable.id eq id }.limit(1).any()
        if (exists) {
            return@transaction Result.failure(IllegalArgumentException("UserId $id already in use."))
        }
        UsersTable.insert {
            it[UsersTable.id] = id
            it[this.firstName] = firstName
            it[this.lastName] = lastName
            it[UsersTable.isStaff] = isStaff
            it[createdAt] = Instant.now()
            it[updatedAt] = Instant.now()
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

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id],
        firstName = this[UsersTable.firstName],
        lastName = this[UsersTable.lastName],
        isStaff = this[UsersTable.isStaff],
        isAdmin = this[UsersTable.isAdmin],
    )
}
