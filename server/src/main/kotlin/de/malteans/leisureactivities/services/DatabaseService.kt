package de.malteans.leisureactivities.services

import org.jetbrains.exposed.sql.Database

interface DatabaseService {
    fun createDatabase(): Database
}