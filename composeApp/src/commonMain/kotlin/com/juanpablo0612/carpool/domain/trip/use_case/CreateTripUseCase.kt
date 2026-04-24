package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository

class CreateTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: Trip): Result<Unit> = repository.createTrip(trip)
}
