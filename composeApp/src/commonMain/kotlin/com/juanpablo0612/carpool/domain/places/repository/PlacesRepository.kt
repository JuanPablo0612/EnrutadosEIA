package com.juanpablo0612.carpool.domain.places.repository

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    suspend fun createPlace(place: Place): Result<Unit>
    suspend fun deletePlace(placeId: String): Result<Unit>
    fun getSavedPlaces(ownerId: String): Flow<List<Place>>
}
