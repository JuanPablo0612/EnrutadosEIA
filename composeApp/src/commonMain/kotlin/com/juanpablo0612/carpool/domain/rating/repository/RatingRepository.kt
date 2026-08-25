package com.juanpablo0612.carpool.domain.rating.repository

import com.juanpablo0612.carpool.domain.rating.model.Rating

interface RatingRepository {
    suspend fun createRating(rating: Rating): Result<Unit>
    suspend fun hasRatedBooking(bookingId: String, raterId: String): Result<Boolean>
    suspend fun getUserAverageRating(userId: String): Result<Double?>
}
