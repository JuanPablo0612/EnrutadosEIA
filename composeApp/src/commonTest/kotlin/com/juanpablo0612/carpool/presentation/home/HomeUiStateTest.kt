package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeUiStateTest {

    private val trip = Trip(
        id = "t1",
        routeId = "",
        driverId = "u1",
        vehicleId = "v1",
        origin = Place.EIA_LAS_PALMAS,
        destination = Place.EIA_LAS_PALMAS,
        waypoints = emptyList(),
        departureTime = 1_000L,
        seatCount = 3,
        confirmedSeats = 0,
        status = TripStatus.Active,
    )

    @Test
    fun newAccountGetsTheWelcomeRatherThanTheEmptyState() {
        assertFalse(HomeUiState(isLoading = false).showNoUpcomingTrips)
    }

    @Test
    fun establishedUserWithNothingComingUpGetsTheEmptyState() {
        assertTrue(HomeUiState(isLoading = false, hasVehicles = true).showNoUpcomingTrips)
        assertTrue(HomeUiState(isLoading = false, hasBookedBefore = true).showNoUpcomingTrips)
    }

    @Test
    fun anUpcomingTripHidesTheEmptyState() {
        val state = HomeUiState(isLoading = false, hasVehicles = true, nextUp = UpcomingTrip.Driving(trip))
        assertFalse(state.showNoUpcomingTrips)
    }
}
