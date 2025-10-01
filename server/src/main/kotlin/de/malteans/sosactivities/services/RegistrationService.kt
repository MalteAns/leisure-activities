package de.malteans.sosactivities.services

import de.malteans.sosactivities.models.Roster

interface RegistrationService {
    fun register(activityId: String, userId: String): String // returns status
    fun cancel(activityId: String, userId: String): Int
    fun roster(activityId: String): Roster
}