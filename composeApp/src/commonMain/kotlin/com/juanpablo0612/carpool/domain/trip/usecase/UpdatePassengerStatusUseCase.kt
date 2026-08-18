package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository

class UpdatePassengerStatusUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String, passengerId: String, status: PickupStatus): Result<Unit> =
        repository.updatePassengerStatus(tripId, passengerId, status.key)
}
