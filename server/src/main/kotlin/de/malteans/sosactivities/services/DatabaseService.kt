package de.malteans.sosactivities.services

import org.jetbrains.exposed.sql.Database

interface DatabaseService {
    fun createDatabase(): Database
}