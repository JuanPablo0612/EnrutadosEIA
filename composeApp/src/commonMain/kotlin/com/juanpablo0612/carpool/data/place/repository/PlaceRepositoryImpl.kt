package com.juanpablo0612.carpool.data.place.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.place.datasource.PlaceRemoteDataSource
import com.juanpablo0612.carpool.data.place.model.PlaceDto
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.place.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaceRepositoryImpl(
    private val remoteDataSource: PlaceRemoteDataSource
) : PlaceRepository {

    override suspend fun deletePlace(placeId: String): Result<Unit> {
        return try {
            remoteDataSource.deletePlace(placeId)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.PlaceException.Unknown)
        }
    }

    override suspend fun createPlace(place: Place): Result<Unit> {
        return try {
            val dto = PlaceDto.fromDomain(place)
            remoteDataSource.createPlace(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.PlaceException.Unknown)
        }
    }

    override fun getSavedPlaces(ownerId: String): Flow<List<Place>> {
        return remoteDataSource.getSavedPlaces(ownerId)
            .map { list -> list.map { it.toDomain() } }
    }
}
