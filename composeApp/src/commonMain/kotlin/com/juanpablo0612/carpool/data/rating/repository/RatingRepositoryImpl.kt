package com.juanpablo0612.carpool.data.rating.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.rating.datasource.RatingRemoteDataSource
import com.juanpablo0612.carpool.data.rating.model.RatingDto
import com.juanpablo0612.carpool.domain.rating.model.Rating
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository

class RatingRepositoryImpl(
    private val remoteDataSource: RatingRemoteDataSource
) : RatingRepository {

    override suspend fun createRating(rating: Rating): Result<Unit> {
        return try {
            val dto = RatingDto.fromDomain(rating)
            remoteDataSource.createRating(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    override suspend fun hasRatedBooking(bookingId: String, raterId: String): Result<Boolean> {
        return try {
            Result.success(remoteDataSource.hasRatedBooking(bookingId, raterId))
        } catch (_: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    override suspend fun getUserAverageRating(userId: String): Result<Double?> {
        return try {
            val ratings = remoteDataSource.getRatingsForUser(userId)
            val avg = if (ratings.isEmpty()) null else ratings.map { it.stars }.average()
            Result.success(avg)
        } catch (_: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }
}
