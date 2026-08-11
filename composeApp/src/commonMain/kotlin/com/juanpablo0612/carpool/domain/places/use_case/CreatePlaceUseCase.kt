package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class CreatePlaceUseCase(
    private val repository: PlacesRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(place: Place): Result<Unit> {
        val ownerId = authRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return repository.createPlace(place.copy(ownerId = ownerId))
    }
}
