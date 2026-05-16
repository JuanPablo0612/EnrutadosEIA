package com.juanpablo0612.carpool.domain.rating.use_case

import com.juanpablo0612.carpool.domain.rating.model.Rating
import com.juanpablo0612.carpool.domain.rating.model.RatingChip
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import kotlin.time.Clock

class CreateRatingUseCase(private val repository: RatingRepository) {
    suspend operator fun invoke(
        tripId: String,
        bookingId: String,
        raterId: String,
        rateeId: String,
        stars: Int,
        chips: List<RatingChip>,
        comment: String?
    ): Result<Unit> {
        val rating = Rating(
            id = "${bookingId}_${raterId}",
            tripId = tripId,
            bookingId = bookingId,
            raterId = raterId,
            rateeId = rateeId,
            stars = stars,
            chips = chips,
            comment = comment?.ifBlank { null },
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        return repository.createRating(rating)
    }
}
