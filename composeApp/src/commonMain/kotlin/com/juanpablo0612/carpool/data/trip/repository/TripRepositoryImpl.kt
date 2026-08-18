package com.juanpablo0612.carpool.data.trip.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.trip.datasource.TripRemoteDataSource
import com.juanpablo0612.carpool.data.trip.model.TripDto
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val remoteDataSource: TripRemoteDataSource
) : TripRepository {

    override suspend fun createTrip(trip: Trip): Result<Unit> {
        return try {
            val dto = TripDto.fromDomain(trip)
            remoteDataSource.createTrip(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.TripException.Unknown)
        }
    }

    override fun getDriverTrips(driverId: String): Flow<List<Trip>> {
        return remoteDataSource.getDriverTrips(driverId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getAvailableTrips(): Flow<List<Trip>> {
        return remoteDataSource.getAvailableTrips()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTripById(id: String): Result<Trip> {
        return try {
            val trip = remoteDataSource.getTripById(id).toDomain()
            Result.success(trip)
        } catch (_: Exception) {
            Result.failure(AppException.TripException.Unknown)
        }
    }

    override suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit> {
        return try {
            val statusString = when (status) {
                is TripStatus.Active -> "ACTIVE"
                is TripStatus.InProgress -> "IN_PROGRESS"
                is TripStatus.Completed -> "COMPLETED"
                is TripStatus.Cancelled -> "CANCELLED"
            }
            remoteDataSource.updateTripStatus(tripId, statusString)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.TripException.Unknown)
        }
    }

    override fun getTripByIdFlow(id: String): Flow<Trip?> {
        return remoteDataSource.getTripByIdFlow(id)
            .map { dto -> dto?.toDomain() }
    }

    override suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double): Result<Unit> {
        return try {
            remoteDataSource.updateDriverLocation(tripId, latitude, longitude)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.TripException.Unknown)
        }
    }

    override suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String): Result<Unit> {
        return try {
            remoteDataSource.updatePassengerStatus(tripId, passengerId, status)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.TripException.Unknown)
        }
    }
}
