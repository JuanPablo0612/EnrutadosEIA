package com.juanpablo0612.carpool.data.rating.datasource

import com.juanpablo0612.carpool.data.rating.model.RatingDto

interface RatingRemoteDataSource {
    suspend fun createRating(rating: RatingDto)
    suspend fun hasRatedBooking(bookingId: String, raterId: String): Boolean
}
