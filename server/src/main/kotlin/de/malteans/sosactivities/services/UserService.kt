package de.malteans.sosactivities.services

import de.malteans.sosactivities.model.User

interface UserService {
    fun insert(id: String, firstName: String, lastName: String, tokenId: Int): Result<User>
    fun get(id: String): User
    fun updateName(id: String, firstName: String, lastName: String): User
    fun updateIsStaff(id: String, value: Boolean): User
    fun getSignUps(id: String): Set<String>
}