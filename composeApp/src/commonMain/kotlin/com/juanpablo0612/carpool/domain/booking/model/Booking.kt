package com.juanpablo0612.carpool.domain.booking.model

import com.juanpablo0612.carpool.domain.trip.model.TripDriver

data class Booking(
    val id: String = "",
    val tripId: String,
    val passengerId: String,
    val driverId: String,
    val passengerName: String,
    val passengerEmail: String,
    /** The passenger's photo at request time, for the driver's lists. */
    val passengerPhotoUrl: String? = null,
    /** The driver as the trip showed them when requested, for the passenger's lists. */
    val driver: TripDriver = TripDriver(),
    /** Where the passenger joins or leaves the trip, when the request came from a search. */
    val meetingStop: BookingMeetingStop? = null,
    val originName: String,
    val destinationName: String,
    val departureTime: Long,
    val status: BookingStatus,
    val createdAt: Long,
    val passengerMessage: String? = null,
    val rejectReason: RejectReason? = null,
    val rejectComment: String? = null,
)
