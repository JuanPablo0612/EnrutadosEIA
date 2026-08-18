package com.juanpablo0612.carpool.data.trip.datasource

import com.juanpablo0612.carpool.data.trip.model.TripDto
import kotlinx.coroutines.flow.Flow

interface TripRemoteDataSource {
    suspend fun createTrip(trip: TripDto): TripDto
    fun getDriverTrips(driverId: String): Flow<List<TripDto>>
    fun getAvailableTrips(): Flow<List<TripDto>>
    suspend fun getTripById(id: String): TripDto
    fun getTripByIdFlow(id: String): Flow<TripDto?>
    suspend fun updateTripStatus(tripId: String, status: String): Unit
    suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double): Unit
    suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String): Unit
}
