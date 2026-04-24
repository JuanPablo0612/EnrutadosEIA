package com.juanpablo0612.carpool.domain.trip.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class GetAvailableTripsUseCase(
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        val currentUserId = authRepository.getCurrentUserId()
        val now = Clock.System.now().toEpochMilliseconds()
        return tripRepository.getAvailableTrips()
            .map { trips ->
                trips.filter { trip ->
                    trip.status is TripStatus.Active &&
                        trip.driverId != currentUserId &&
                        trip.departureTime >= now
                }
            }
    }
}
