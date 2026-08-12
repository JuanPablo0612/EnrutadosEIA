package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetSavedPlacesUseCase(
    private val repository: PlacesRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Place>> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return flowOf(emptyList())
        return repository.getSavedPlaces(currentUserId)
    }
}
