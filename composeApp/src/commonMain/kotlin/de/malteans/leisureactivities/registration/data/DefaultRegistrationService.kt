package de.malteans.leisureactivities.registration.data

import de.malteans.leisureactivities.core.domain.DataStoreRepository
import de.malteans.leisureactivities.core.domain.RemoteService
import de.malteans.leisureactivities.model.User
import de.malteans.leisureactivities.model.ext.toDomain
import de.malteans.leisureactivities.registration.domain.RegistrationService

class DefaultRegistrationService(
    private val remoteService: RemoteService,
    private val dataStoreRepository: DataStoreRepository,
): RegistrationService {
    override suspend fun createUser(
        registrationToken: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        val createUserResp = remoteService.createUser(registrationToken, firstName, lastName)
            .getOrElse { return Result.failure(it) }
        val user = createUserResp.user.toDomain()
        val token = createUserResp.token
        try {
            dataStoreRepository.saveUser(user)
            dataStoreRepository.saveToken(token)
            return Result.success(user)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun checkToken(token: String): Result<Boolean> {
        return remoteService.checkRegToken(token)
            .map { it.valid }
    }
}