package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository

class UpdateTripStatusUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(tripId: String, status: TripStatus): Result<Unit> =
        tripRepository.updateTripStatus(tripId, status)
}
