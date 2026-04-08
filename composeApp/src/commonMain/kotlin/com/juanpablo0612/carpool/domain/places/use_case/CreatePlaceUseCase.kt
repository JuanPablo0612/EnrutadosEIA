package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class CreatePlaceUseCase(private val repository: PlacesRepository) {
    suspend operator fun invoke(place: Place): Result<Unit> {
        if (place.name.isBlank()) {
            return Result.failure(Exception("Place name cannot be empty"))
        }
        return repository.createPlace(place)
    }
}
