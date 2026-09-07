package de.malteans.leisureactivities.core.data

import de.malteans.leisureactivities.core.domain.DataStoreRepository
import de.malteans.leisureactivities.core.domain.MainService
import de.malteans.leisureactivities.core.domain.RemoteService
import de.malteans.leisureactivities.model.ext.toDomain

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