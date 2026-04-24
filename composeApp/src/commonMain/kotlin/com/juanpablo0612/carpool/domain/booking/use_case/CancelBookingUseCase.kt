package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository

class CancelBookingUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(bookingId: String): Result<Unit> =
        repository.updateBookingStatus(bookingId, BookingStatus.Cancelled)
}
