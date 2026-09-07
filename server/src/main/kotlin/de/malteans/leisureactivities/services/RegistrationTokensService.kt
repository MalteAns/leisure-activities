package de.malteans.leisureactivities.services

interface RegistrationTokensService {

    suspend fun checkToken(token: String): Boolean

    suspend fun getActiveTokenId(token: String): Int?
}