package com.juanpablo0612.carpool.domain.place.repository

import com.juanpablo0612.carpool.domain.place.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun createPlace(place: Place): Result<Unit>
    suspend fun deletePlace(placeId: String): Result<Unit>
    fun getSavedPlaces(ownerId: String): Flow<List<Place>>
}
