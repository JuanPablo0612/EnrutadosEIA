package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.data.place.model.PlaceDto
import kotlinx.coroutines.flow.Flow

interface PlaceRemoteDataSource {
    suspend fun createPlace(place: PlaceDto): PlaceDto
    suspend fun deletePlace(placeId: String)
    fun getSavedPlaces(ownerId: String): Flow<List<PlaceDto>>
}
