package com.juanpablo0612.carpool.data.rating.datasource

import com.juanpablo0612.carpool.data.rating.model.RatingDto
import com.juanpablo0612.carpool.data.rating.model.UserRatingSummaryDto

interface RatingRemoteDataSource {
    suspend fun createRating(rating: RatingDto)
    suspend fun hasRatedBooking(bookingId: String, raterId: String): Boolean
    suspend fun getUserRatingSummary(userId: String): UserRatingSummaryDto
}
