package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class GetTripByIdFlowUseCase(private val repository: TripRepository) {
    operator fun invoke(tripId: String): Flow<Trip?> = repository.getTripByIdFlow(tripId)
}
