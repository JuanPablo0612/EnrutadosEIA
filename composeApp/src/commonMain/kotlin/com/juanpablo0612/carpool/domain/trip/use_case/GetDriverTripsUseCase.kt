package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class GetDriverTripsUseCase(private val repository: TripRepository) {
    operator fun invoke(driverId: String): Flow<List<Trip>> = repository.getDriverTrips(driverId)
}
