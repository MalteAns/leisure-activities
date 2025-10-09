package de.malteans.sosactivities.services

import de.malteans.sosactivities.model.RegStatus
import de.malteans.sosactivities.model.Roster

interface SignUpService {
    fun register(activityId: String, userId: String): RegStatus
    fun cancel(activityId: String, userId: String): Int
    fun roster(activityId: String): Roster
}