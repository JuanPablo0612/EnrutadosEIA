package com.juanpablo0612.carpool.domain.trip.repository

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    /** Creates the trip and returns its id. */
    suspend fun createTrip(trip: Trip): Result<String>

    /** Creates all [trips] atomically (one batch) and returns their ids in order. */
    suspend fun createTrips(trips: List<Trip>): Result<List<String>>
    fun getDriverTrips(driverId: String): Flow<List<Trip>>
    fun getAvailableTrips(): Flow<List<Trip>>
    suspend fun getTripById(id: String): Result<Trip>
    fun getTripByIdFlow(id: String): Flow<Trip?>
    /** Replaces the seats offered and the intermediate stops of a published trip. */
    suspend fun updateTripDetails(tripId: String, seatCount: Int, waypoints: List<Place>): Result<Unit>
    suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit>
    suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double): Result<Unit>
    suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String): Result<Unit>
}
