package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class DeletePlaceUseCase(
    private val repository: PlacesRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(place: Place): Result<Unit> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        if (place.ownerId != currentUserId) {
            return Result.failure(IllegalStateException("Cannot delete a place you do not own"))
        }
        return repository.deletePlace(place.id)
    }
}
