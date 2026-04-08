package com.juanpablo0612.carpool.domain.places.repository

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    suspend fun createPlace(place: Place): Result<Unit>
    fun getSavedPlaces(): Flow<List<Place>>
    suspend fun searchPlaces(query: String): Result<List<Place>>
}
