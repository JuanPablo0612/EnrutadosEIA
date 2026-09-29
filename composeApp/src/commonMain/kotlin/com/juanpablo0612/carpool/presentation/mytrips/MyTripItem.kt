package com.juanpablo0612.carpool.presentation.mytrips

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus

/** One entry of "Mis viajes": a trip the user drives or a seat they asked for. */
sealed class MyTripItem {
    abstract val key: String
    abstract val tripId: String
    abstract val departureTime: Long

    data class Driving(val trip: Trip) : MyTripItem() {
        override val key: String get() = "trip_${trip.id}"
        override val tripId: String get() = trip.id
        override val departureTime: Long get() = trip.departureTime
    }

    data class Riding(val booking: Booking) : MyTripItem() {
        override val key: String get() = "booking_${booking.id}"
        override val tripId: String get() = booking.tripId
        override val departureTime: Long get() = booking.departureTime
    }
}

sealed class MyTripsSegment {
    data object Upcoming : MyTripsSegment()
    data object History : MyTripsSegment()
}

sealed class MyTripsFilter {
    data object All : MyTripsFilter()
    data object Driving : MyTripsFilter()
    data object Riding : MyTripsFilter()
}

/**
 * Whether [this] still lies ahead at [now]. A trip the user drives stays upcoming while it runs,
 * even past its departure time; a seat stays upcoming until the trip leaves or the request closes
 * (rejected or cancelled), after which it belongs to the history, where it can be rated.
 */
fun MyTripItem.isUpcoming(now: Long): Boolean = when (this) {
    is MyTripItem.Driving -> when (trip.status) {
        TripStatus.InProgress -> true
        TripStatus.Active -> trip.departureTime > now
        TripStatus.Completed, TripStatus.Cancelled -> false
    }
    is MyTripItem.Riding -> !booking.status.isTerminal && booking.departureTime > now
}

/**
 * The items of [segment] that pass [filter]: upcoming ones soonest first, history newest first.
 */
fun List<MyTripItem>.select(segment: MyTripsSegment, filter: MyTripsFilter, now: Long): List<MyTripItem> {
    val byRole = filter { item ->
        when (filter) {
            MyTripsFilter.All -> true
            MyTripsFilter.Driving -> item is MyTripItem.Driving
            MyTripsFilter.Riding -> item is MyTripItem.Riding
        }
    }
    return when (segment) {
        MyTripsSegment.Upcoming -> byRole.filter { it.isUpcoming(now) }.sortedBy { it.departureTime }
        MyTripsSegment.History -> byRole.filterNot { it.isUpcoming(now) }.sortedByDescending { it.departureTime }
    }
}

/** A departed seat the passenger actually took, and so can rate the driver for. */
val MyTripItem.Riding.canRate: Boolean
    get() = booking.status == BookingStatus.Confirmed
