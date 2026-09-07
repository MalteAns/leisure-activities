package de.malteans.leisureactivities.services.impl

import de.malteans.leisureactivities.services.DatabaseService
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

class DatabaseServiceImpl(
    private val config: ApplicationConfig
) : DatabaseService {
    override fun createDatabase(): Database {
        val url  = config.property("db.url").getString()
        val user = config.property("db.user").getString()
        val pass = config.property("db.password").getString()
        val drv  = config.propertyOrNull("db.driver")?.getString() ?: "org.mariadb.jdbc.Driver"
        return Database.connect(url = url, driver = drv, user = user, password = pass)
    }
}