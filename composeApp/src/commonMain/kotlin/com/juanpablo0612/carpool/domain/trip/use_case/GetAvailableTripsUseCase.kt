package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetAvailableTripsUseCase(
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return flowOf(emptyList())
        // status == ACTIVE and departureTime >= now are already enforced server-side by
        // TripRepositoryImpl.getAvailableTrips(); only the "not my own trip" filter belongs here.
        return tripRepository.getAvailableTrips()
            .map { trips -> trips.filter { it.driverId != currentUserId } }
    }
}
