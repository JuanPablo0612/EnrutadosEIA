package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TripBookingsTest {

    private fun booking(id: String, tripId: String, departure: Long, createdAt: Long) = Booking(
        id = id,
        tripId = tripId,
        passengerId = "p_$id",
        driverId = "me",
        passengerName = "",
        passengerEmail = "",
        originName = "Origin $tripId",
        destinationName = "Destination $tripId",
        departureTime = departure,
        status = BookingStatus.Pending,
        createdAt = createdAt,
    )

    @Test
    fun groupsByTripSoonestFirstAndKeepsArrivalOrder() {
        val groups = listOf(
            booking("late", tripId = "b", departure = 200, createdAt = 1),
            booking("second", tripId = "a", departure = 100, createdAt = 20),
            booking("first", tripId = "a", departure = 100, createdAt = 10),
        ).groupByTrip(freeSeats = mapOf("a" to 2))

        assertEquals(listOf("a", "b"), groups.map { it.tripId })
        assertEquals(listOf("first", "second"), groups.first().bookings.map { it.id })
        assertEquals("Origin a", groups.first().originName)
        assertEquals(2, groups.first().freeSeats)
        assertNull(groups.last().freeSeats)
    }

    @Test
    fun theLastSeatIsContestedOnlyWithSeveralRequests() {
        val one = listOf(booking("x", "a", 100, 1)).groupByTrip(mapOf("a" to 1)).single()
        val two = listOf(booking("x", "a", 100, 1), booking("y", "a", 100, 2)).groupByTrip(mapOf("a" to 1)).single()

        assertFalse(one.isLastSeatContested)
        assertTrue(two.isLastSeatContested)
    }

    @Test
    fun aTripIsFullOnlyOnceItsSeatsAreKnown() {
        assertTrue(listOf(booking("x", "a", 100, 1)).groupByTrip(mapOf("a" to 0)).single().isFull)
        assertFalse(listOf(booking("x", "a", 100, 1)).groupByTrip(emptyMap()).single().isFull)
    }
}
