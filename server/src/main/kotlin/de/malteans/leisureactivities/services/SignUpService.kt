package de.malteans.leisureactivities.services

import de.malteans.leisureactivities.model.RegStatus
import de.malteans.leisureactivities.model.Roster

interface SignUpService {
    fun register(activityId: String, userId: String): RegStatus
    fun cancel(activityId: String, userId: String): Int
    fun roster(activityId: String): Roster
}