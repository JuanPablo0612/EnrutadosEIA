package com.juanpablo0612.carpool.domain.rating.use_case

import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository

class HasRatedBookingUseCase(private val repository: RatingRepository) {
    suspend operator fun invoke(bookingId: String, raterId: String): Result<Boolean> =
        repository.hasRatedBooking(bookingId, raterId)
}
