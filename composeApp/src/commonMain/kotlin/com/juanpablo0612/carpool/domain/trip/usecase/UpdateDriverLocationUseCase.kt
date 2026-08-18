package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.trip.repository.TripRepository

class UpdateDriverLocationUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String, latitude: Double, longitude: Double): Result<Unit> =
        repository.updateDriverLocation(tripId, latitude, longitude)
}
