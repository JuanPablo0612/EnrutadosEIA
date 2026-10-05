package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlin.time.Clock

/**
 * Cancels [booking], by its passenger or the trip's driver. Only an open request on a trip that has
 * not left yet at [now] can be cancelled: once the trip departs the seat is history (and may be
 * rated), and a rejected or cancelled request is already closed.
 */
class CancelBookingUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(
        booking: Booking,
        now: Long = Clock.System.now().toEpochMilliseconds(),
    ): Result<Unit> {
        if (booking.status.isTerminal || booking.departureTime <= now) {
            return Result.failure(AppException.BookingException.TripClosed)
        }
        return repository.updateBookingStatus(booking.id, BookingStatus.Cancelled)
    }
}
