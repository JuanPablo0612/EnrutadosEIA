package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Reads trip.seatCount - trip.confirmedSeats straight off the trip document instead of counting
// CONFIRMED bookings, so a passenger browsing a trip never needs to read (PII-carrying) booking
// documents just to learn a seat count. See BookingRepositoryImpl.applyStatusTransition for how
// confirmedSeats is kept in sync.
class GetTripAvailableSeatsUseCase(private val repository: TripRepository) {
    operator fun invoke(tripId: String): Flow<Int> =
        repository.getTripByIdFlow(tripId)
            .map { trip -> ((trip?.seatCount ?: 0) - (trip?.confirmedSeats ?: 0)).coerceAtLeast(0) }
}
