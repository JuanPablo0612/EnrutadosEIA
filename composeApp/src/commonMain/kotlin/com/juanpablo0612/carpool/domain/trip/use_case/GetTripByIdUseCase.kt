package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository

class GetTripByIdUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String): Result<Trip> = repository.getTripById(tripId)
}
