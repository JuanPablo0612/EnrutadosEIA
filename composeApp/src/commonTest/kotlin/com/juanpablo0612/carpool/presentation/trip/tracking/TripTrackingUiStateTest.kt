package com.juanpablo0612.carpool.presentation.trip.tracking

import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TripTrackingUiStateTest {

    private fun trip(
        status: TripStatus = TripStatus.InProgress,
        latitude: Double? = null,
        longitude: Double? = null,
    ) = Trip(
        id = "t1",
        routeId = "",
        driverId = "d1",
        vehicleId = "v1",
        origin = Place(name = "Casa", address = "", latitude = 6.2, longitude = -75.6),
        destination = Place.UNIVERSITY_EIA,
        waypoints = emptyList(),
        departureTime = 0L,
        status = status,
        driverLatitude = latitude,
        driverLongitude = longitude,
    )

    private fun passengerState(trip: Trip?) = TripTrackingUiState(trip = trip, isDriver = false, isLoading = false)

    @Test
    fun locatesDriverWhileTripIsUnderWayWithoutAPosition() {
        assertTrue(passengerState(trip()).isLocatingDriver)
    }

    @Test
    fun stopsLocatingOnceTheDriverSharesAPosition() {
        val state = passengerState(trip(latitude = 6.1, longitude = -75.5))
        assertFalse(state.isLocatingDriver)
        assertEquals(Coordinates(6.1, -75.5), state.driverPosition)
    }

    @Test
    fun aHalfReportedPositionIsNoPosition() {
        val state = passengerState(trip(latitude = 6.1))
        assertNull(state.driverPosition)
        assertTrue(state.isLocatingDriver)
    }

    @Test
    fun doesNotLocateBeforeTheTripStartsOrAfterItEnds() {
        assertFalse(passengerState(trip(status = TripStatus.Active)).isLocatingDriver)
        assertFalse(passengerState(trip(status = TripStatus.Completed)).isLocatingDriver)
    }

    @Test
    fun doesNotLocateForTheDriverOrWhileLoading() {
        assertFalse(passengerState(trip()).copy(isDriver = true).isLocatingDriver)
        assertFalse(passengerState(trip()).copy(isLoading = true).isLocatingDriver)
    }
}
