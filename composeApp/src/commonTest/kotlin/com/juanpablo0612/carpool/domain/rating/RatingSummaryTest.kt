package com.juanpablo0612.carpool.domain.rating

import com.juanpablo0612.carpool.domain.rating.model.RatingSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RatingSummaryTest {

    @Test
    fun noRatingsIsNoSummaryRatherThanZeroStars() {
        assertNull(RatingSummary.of(sum = 0, count = 0))
    }

    @Test
    fun averagesTheSumOverTheCount() {
        assertEquals(RatingSummary(average = 4.5, count = 2), RatingSummary.of(sum = 9, count = 2))
    }
}
