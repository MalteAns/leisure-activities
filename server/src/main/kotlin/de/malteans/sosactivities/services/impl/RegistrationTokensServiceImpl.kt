package de.malteans.sosactivities.services.impl

import de.malteans.sosactivities.db.RegistrationTokensTable
import de.malteans.sosactivities.services.RegistrationTokensService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RegistrationTokensServiceImpl(
    private val db: Database
): RegistrationTokensService {

    override suspend fun checkToken(token: String): Boolean = transaction(db) {
        RegistrationTokensTable
            .selectAll()
            .where {
                (RegistrationTokensTable.token eq token) and
                (RegistrationTokensTable.expired eq false)
            }
            .count() > 0
    }

    override suspend fun getActiveTokenId(token: String): Int? = transaction(db) {
        RegistrationTokensTable
            .selectAll()
            .where {
                (RegistrationTokensTable.token eq token) and
                (RegistrationTokensTable.expired eq false)
            }
            .map { it[RegistrationTokensTable.id] }
            .firstOrNull()
    }
}