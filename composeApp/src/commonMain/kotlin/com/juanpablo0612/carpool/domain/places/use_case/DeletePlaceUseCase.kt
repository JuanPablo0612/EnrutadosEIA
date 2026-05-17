package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class DeletePlaceUseCase(private val repository: PlacesRepository) {
    suspend operator fun invoke(placeId: String): Result<Unit> {
        return repository.deletePlace(placeId)
    }
}
