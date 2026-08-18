package com.juanpablo0612.carpool.domain.place.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.place.repository.PlaceRepository

class DeletePlaceUseCase(
    private val repository: PlaceRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(place: Place): Result<Unit> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.PlaceException.NotAuthenticated)
        if (place.ownerId != currentUserId) {
            return Result.failure(AppException.PlaceException.Unauthorized)
        }
        return repository.deletePlace(place.id)
    }
}
