package com.juanpablo0612.carpool.data.rating.model

import kotlinx.serialization.Serializable

/**
 * The rating aggregate the onRatingCreated function keeps on users/{uid}. Integers, so the
 * average is computed here rather than decoding a server-written number that may be stored as an
 * int or a double.
 */
@Serializable
data class UserRatingSummaryDto(
    val ratingSum: Long = 0,
    val ratingCount: Long = 0,
) {
    val average: Double? get() = if (ratingCount > 0) ratingSum.toDouble() / ratingCount else null
}
