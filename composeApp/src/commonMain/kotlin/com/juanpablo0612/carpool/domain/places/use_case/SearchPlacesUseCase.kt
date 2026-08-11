package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class SearchPlacesUseCase(
    private val repository: PlacesRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(query: String): Result<List<Place>> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return Result.success(emptyList())
        return repository.searchPlaces(query)
            .map { places -> places.filter { it.ownerId == currentUserId } }
    }
}
