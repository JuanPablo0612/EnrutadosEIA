package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class GetPassengerBookingsUseCase(private val repository: BookingRepository) {
    operator fun invoke(passengerId: String): Flow<List<Booking>> =
        repository.getPassengerBookings(passengerId)
}
