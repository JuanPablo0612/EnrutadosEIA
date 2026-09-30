package com.juanpablo0612.carpool.presentation.home

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeWelcomeTest {

    private fun welcome(
        hasBookedBefore: Boolean = false,
        hasVehicles: Boolean = false,
        hasUpcomingTrip: Boolean = false,
        vehicleSuggestionDismissed: Boolean = false,
    ) = HomeWelcome.of(hasBookedBefore, hasVehicles, hasUpcomingTrip, vehicleSuggestionDismissed)

    @Test
    fun aNewAccountChoosesHowToStart() {
        assertEquals(HomeWelcome.ChooseHowToStart, welcome())
    }

    @Test
    fun aRiderWithoutACarIsSuggestedOneUntilTheyHideIt() {
        assertEquals(HomeWelcome.SuggestVehicle, welcome(hasBookedBefore = true, hasUpcomingTrip = true))
        assertEquals(HomeWelcome.None, welcome(hasBookedBefore = true, vehicleSuggestionDismissed = true))
    }

    @Test
    fun aDriverIsNeverNudged() {
        assertEquals(HomeWelcome.None, welcome(hasVehicles = true))
        assertEquals(HomeWelcome.None, welcome(hasVehicles = true, hasBookedBefore = true))
    }

    @Test
    fun anUpcomingTripEndsTheNewAccountWelcome() {
        assertEquals(HomeWelcome.None, welcome(hasUpcomingTrip = true))
    }
}
