package com.juanpablo0612.carpool.domain.rating.usecase

import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository

class GetUserAverageRatingUseCase(private val repository: RatingRepository) {
    suspend operator fun invoke(userId: String): Result<Double?> =
        repository.getUserAverageRating(userId)
}
