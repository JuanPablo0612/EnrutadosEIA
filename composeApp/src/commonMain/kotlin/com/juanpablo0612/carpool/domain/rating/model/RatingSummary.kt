package com.juanpablo0612.carpool.domain.rating.model

/** How someone has been rated: the average of all their ratings and how many there are. */
data class RatingSummary(
    val average: Double,
    val count: Int,
) {
    companion object {
        /**
         * Builds a summary from the integer aggregate kept on the user document; null when there
         * are no ratings yet, so "no rating" never reads as a zero-star average.
         */
        fun of(sum: Long, count: Long): RatingSummary? =
            if (count > 0) RatingSummary(average = sum.toDouble() / count, count = count.toInt()) else null
    }
}
