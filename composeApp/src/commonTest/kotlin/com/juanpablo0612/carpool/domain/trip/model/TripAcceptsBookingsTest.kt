package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.place.model.Place
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TripAcceptsBookingsTest {

    private fun trip(status: TripStatus = TripStatus.Active, departureTime: Long = 1_000L) = Trip(
        routeId = "",
        driverId = "d1",
        vehicleId = "v1",
        origin = Place.EIA_LAS_PALMAS,
        destination = Place.EIA_LAS_PALMAS,
        waypoints = emptyList(),
        departureTime = departureTime,
        status = status,
    )

    @Test
    fun anActiveTripAheadAcceptsBookings() {
        assertTrue(trip().acceptsBookings(now = 999L))
    }

    @Test
    fun anActiveTripThatHasDepartedDoesNot() {
        assertFalse(trip().acceptsBookings(now = 1_000L))
        assertFalse(trip().acceptsBookings(now = 2_000L))
    }

    @Test
    fun aTripThatIsNoLongerActiveDoesNot() {
        listOf(TripStatus.InProgress, TripStatus.Completed, TripStatus.Cancelled).forEach { status ->
            assertFalse(trip(status = status).acceptsBookings(now = 0L), "$status")
        }
    }
}
