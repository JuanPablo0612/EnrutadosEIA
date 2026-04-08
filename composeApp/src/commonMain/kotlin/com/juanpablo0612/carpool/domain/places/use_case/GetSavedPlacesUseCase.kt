package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow

class GetSavedPlacesUseCase(private val repository: PlacesRepository) {
    operator fun invoke(): Flow<List<Place>> {
        return repository.getSavedPlaces()
    }
}
