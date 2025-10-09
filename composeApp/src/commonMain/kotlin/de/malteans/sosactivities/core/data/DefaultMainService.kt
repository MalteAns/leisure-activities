package de.malteans.sosactivities.core.data

import de.malteans.sosactivities.core.domain.DataStoreRepository
import de.malteans.sosactivities.core.domain.MainService
import de.malteans.sosactivities.core.domain.RemoteService
import de.malteans.sosactivities.model.ext.toDomain

class DefaultMainService(
    private val dataStoreRepository: DataStoreRepository,
    private val remoteService: RemoteService,
): MainService {

    override fun isUserCreated() = dataStoreRepository.isUserCreated()

    override fun getUserIsStaff() = dataStoreRepository.getUserIsStaff()

    /** @return Result of new `isStaff` value */
    override suspend fun refreshToken(): Result<Boolean> {
        return remoteService.refreshToken()
            .onSuccess { refreshTokenResp ->
                dataStoreRepository.saveUser(refreshTokenResp.user.toDomain())
                dataStoreRepository.saveToken(refreshTokenResp.token)
            }
            .map { refreshTokenResp ->
                refreshTokenResp.user.isStaff
            }
    }
}