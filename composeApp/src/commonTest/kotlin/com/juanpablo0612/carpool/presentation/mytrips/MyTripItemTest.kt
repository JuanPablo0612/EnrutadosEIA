package com.juanpablo0612.carpool.presentation.mytrips

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MyTripItemTest {

    private val now = 10_000L

    private fun driving(id: String, departure: Long, status: TripStatus = TripStatus.Active) = MyTripItem.Driving(
        Trip(
            id = id,
            routeId = "",
            driverId = "me",
            vehicleId = "v",
            origin = Place.EIA_LAS_PALMAS,
            destination = Place.EIA_ZUNIGA,
            waypoints = emptyList(),
            departureTime = departure,
            status = status,
        )
    )

    private fun riding(id: String, departure: Long, status: BookingStatus = BookingStatus.Confirmed) = MyTripItem.Riding(
        Booking(
            id = id,
            tripId = "t_$id",
            passengerId = "me",
            driverId = "d",
            passengerName = "",
            passengerEmail = "",
            originName = "",
            destinationName = "",
            departureTime = departure,
            status = status,
            createdAt = 0L,
        )
    )

    @Test
    fun aRunningTripStaysUpcomingPastItsDeparture() {
        assertTrue(driving("a", departure = now - 5_000, status = TripStatus.InProgress).isUpcoming(now))
    }

    @Test
    fun aDepartedActiveTripIsHistory() {
        assertFalse(driving("a", departure = now - 1).isUpcoming(now))
    }

    @Test
    fun finishedAndCancelledTripsAreHistoryEvenAhead() {
        assertFalse(driving("a", departure = now + 1, status = TripStatus.Completed).isUpcoming(now))
        assertFalse(driving("b", departure = now + 1, status = TripStatus.Cancelled).isUpcoming(now))
    }

    @Test
    fun aClosedRequestIsHistoryEvenAhead() {
        assertFalse(riding("a", departure = now + 1, status = BookingStatus.Rejected).isUpcoming(now))
        assertTrue(riding("b", departure = now + 1, status = BookingStatus.Pending).isUpcoming(now))
    }

    @Test
    fun upcomingIsSoonestFirstAndFiltersByRole() {
        val items = listOf(riding("late", now + 300), driving("soon", now + 100), riding("mid", now + 200))
        assertEquals(
            listOf("trip_soon", "booking_mid", "booking_late"),
            items.select(MyTripsSegment.Upcoming, MyTripsFilter.All, now).map { it.key },
        )
        assertEquals(
            listOf("booking_mid", "booking_late"),
            items.select(MyTripsSegment.Upcoming, MyTripsFilter.Riding, now).map { it.key },
        )
    }

    @Test
    fun historyIsNewestFirst() {
        val items = listOf(driving("old", now - 300), riding("recent", now - 100), driving("ahead", now + 100))
        assertEquals(
            listOf("booking_recent", "trip_old"),
            items.select(MyTripsSegment.History, MyTripsFilter.All, now).map { it.key },
        )
    }
}
