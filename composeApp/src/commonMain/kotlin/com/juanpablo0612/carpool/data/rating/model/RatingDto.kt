package com.juanpablo0612.carpool.data.rating.model

import com.juanpablo0612.carpool.domain.rating.model.Rating
import com.juanpablo0612.carpool.domain.rating.model.RatingChip
import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    val id: String = "",
    val tripId: String = "",
    val bookingId: String = "",
    val raterId: String = "",
    val rateeId: String = "",
    val stars: Int = 0,
    val chips: List<String> = emptyList(),
    val comment: String? = null,
    val createdAt: Long = 0L
) {
    fun toDomain(): Rating = Rating(
        id = id,
        tripId = tripId,
        bookingId = bookingId,
        raterId = raterId,
        rateeId = rateeId,
        stars = stars,
        chips = chips.mapNotNull { RatingChip.fromKey(it) },
        comment = comment,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(rating: Rating): RatingDto = RatingDto(
            id = rating.id,
            tripId = rating.tripId,
            bookingId = rating.bookingId,
            raterId = rating.raterId,
            rateeId = rating.rateeId,
            stars = rating.stars,
            chips = rating.chips.map { it.key },
            comment = rating.comment,
            createdAt = rating.createdAt
        )
    }
}
