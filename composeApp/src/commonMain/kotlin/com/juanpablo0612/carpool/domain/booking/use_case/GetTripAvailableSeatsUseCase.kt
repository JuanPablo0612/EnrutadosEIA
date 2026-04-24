package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTripAvailableSeatsUseCase(private val repository: BookingRepository) {
    operator fun invoke(tripId: String, totalSeats: Int): Flow<Int> =
        repository.getBookingsForTrip(tripId)
            .map { bookings ->
                val confirmed = bookings.count { it.status is BookingStatus.Confirmed }
                (totalSeats - confirmed).coerceAtLeast(0)
            }
}
