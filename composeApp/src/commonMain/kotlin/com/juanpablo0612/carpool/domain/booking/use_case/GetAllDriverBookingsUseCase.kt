package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class GetAllDriverBookingsUseCase(private val repository: BookingRepository) {
    operator fun invoke(driverId: String): Flow<List<Booking>> =
        repository.getAllDriverBookings(driverId)
}
