package com.juanpablo0612.carpool.domain.places.use_case

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository

class SearchPlacesUseCase(private val repository: PlacesRepository) {
    suspend operator fun invoke(query: String): Result<List<Place>> {
        return repository.searchPlaces(query)
    }
}
