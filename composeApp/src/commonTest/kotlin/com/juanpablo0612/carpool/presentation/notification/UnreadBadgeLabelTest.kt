package com.juanpablo0612.carpool.presentation.notification

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UnreadBadgeLabelTest {

    @Test
    fun nothingUnreadShowsNoBadge() {
        assertNull(unreadBadgeLabel(0))
        assertNull(unreadBadgeLabel(-1))
    }

    @Test
    fun smallCountsShowTheNumber() {
        assertEquals("1", unreadBadgeLabel(1))
        assertEquals("9", unreadBadgeLabel(9))
    }

    @Test
    fun largeCountsAreCapped() {
        assertEquals("9+", unreadBadgeLabel(10))
        assertEquals("9+", unreadBadgeLabel(250))
    }
}
