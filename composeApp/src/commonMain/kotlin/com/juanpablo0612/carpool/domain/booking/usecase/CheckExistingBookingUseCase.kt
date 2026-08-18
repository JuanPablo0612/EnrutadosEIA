package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository

class CheckExistingBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(tripId: String): Result<Boolean> {
        val userId = authRepository.getCurrentUserId() ?: return Result.success(false)
        return bookingRepository.hasActiveBooking(userId, tripId)
    }
}
