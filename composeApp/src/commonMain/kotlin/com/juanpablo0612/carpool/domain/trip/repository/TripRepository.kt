package com.juanpablo0612.carpool.domain.trip.repository

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun createTrip(trip: Trip): Result<Unit>
    fun getDriverTrips(driverId: String): Flow<List<Trip>>
    fun getAvailableTrips(): Flow<List<Trip>>
    suspend fun getTripById(id: String): Result<Trip>
    suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit>
}
