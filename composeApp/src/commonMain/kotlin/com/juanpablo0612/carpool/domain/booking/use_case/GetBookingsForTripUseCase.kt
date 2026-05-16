package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class GetBookingsForTripUseCase(private val repository: BookingRepository) {
    operator fun invoke(tripId: String): Flow<List<Booking>> =
        repository.getBookingsForTrip(tripId)
}
