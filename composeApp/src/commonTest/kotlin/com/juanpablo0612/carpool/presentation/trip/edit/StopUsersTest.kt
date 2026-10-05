package com.juanpablo0612.carpool.presentation.trip.edit

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingMeetingStop
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StopUsersTest {

    private fun booking(
        passengerId: String,
        name: String = "Laura Gómez",
        stop: BookingMeetingStop? = BookingMeetingStop(name = "Parque", isDropoff = true),
        status: BookingStatus = BookingStatus.Confirmed,
    ) = Booking(
        id = "b_$passengerId",
        tripId = "t1",
        passengerId = passengerId,
        driverId = "d1",
        passengerName = name,
        passengerEmail = "",
        meetingStop = stop,
        originName = "",
        destinationName = "",
        departureTime = 0,
        status = status,
        createdAt = 0,
    )

    @Test
    fun aSinglePassengerIsNamedByFirstNameWithTheirDirection() {
        val users = stopUsersByName(listOf(booking("p1")))
        assertEquals(mapOf("Parque" to StopUsers.One(firstName = "Laura", isDropoff = true)), users)
    }

    @Test
    fun severalPassengersAtOneStopAreCounted() {
        val users = stopUsersByName(
            listOf(booking("p1"), booking("p2", name = "Ana", status = BookingStatus.Pending))
        )
        assertEquals(mapOf("Parque" to StopUsers.Several(count = 2)), users)
    }

    @Test
    fun closedRequestsAndBookingsWithoutAStopDoNotLockAnything() {
        val users = stopUsersByName(
            listOf(
                booking("p1", status = BookingStatus.Cancelled),
                booking("p2", status = BookingStatus.Rejected),
                booking("p3", stop = null),
            )
        )
        assertTrue(users.isEmpty())
    }

    @Test
    fun aPassengerWithoutANameIsCountedInstead() {
        val users = stopUsersByName(listOf(booking("p1", name = "  ")))
        assertEquals(mapOf("Parque" to StopUsers.Several(count = 1)), users)
    }
}
