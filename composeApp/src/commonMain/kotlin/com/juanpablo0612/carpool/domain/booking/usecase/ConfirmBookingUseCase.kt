package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.first

class ConfirmBookingUseCase(
    private val repository: BookingRepository,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
) {
    suspend operator fun invoke(bookingId: String, tripId: String): Result<Unit> {
        val availableSeats = getTripAvailableSeatsUseCase(tripId).first()
        if (availableSeats <= 0) {
            return Result.failure(AppException.BookingException.NoSeatsAvailable)
        }
        return repository.updateBookingStatus(bookingId, BookingStatus.Confirmed)
    }
}
