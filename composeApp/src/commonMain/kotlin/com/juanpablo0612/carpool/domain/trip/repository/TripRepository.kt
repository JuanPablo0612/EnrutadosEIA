package com.juanpablo0612.carpool.domain.trip.repository

import com.juanpablo0612.carpool.domain.trip.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun createTrip(trip: Trip): Result<Unit>
    fun getDriverTrips(driverId: String): Flow<List<Trip>>
    fun getAvailableTrips(): Flow<List<Trip>>
    suspend fun getTripById(id: String): Result<Trip>
}
