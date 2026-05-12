package com.juanpablo0612.carpool.data.booking.model

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val id: String = "",
    val tripId: String = "",
    val passengerId: String = "",
    val driverId: String = "",
    val passengerName: String = "",
    val passengerEmail: String = "",
    val originName: String = "",
    val destinationName: String = "",
    val departureTime: Long = 0L,
    val status: String = "PENDING",
    val createdAt: Long = 0L,
    val passengerMessage: String? = null,
    val rejectReason: String? = null,
    val rejectComment: String? = null,
) {
    fun toDomain(): Booking = Booking(
        id = id,
        tripId = tripId,
        passengerId = passengerId,
        driverId = driverId,
        passengerName = passengerName,
        passengerEmail = passengerEmail,
        originName = originName,
        destinationName = destinationName,
        departureTime = departureTime,
        status = when (status) {
            "CONFIRMED" -> BookingStatus.Confirmed
            "REJECTED" -> BookingStatus.Rejected
            "CANCELLED" -> BookingStatus.Cancelled
            else -> BookingStatus.Pending
        },
        createdAt = createdAt,
        passengerMessage = passengerMessage,
        rejectReason = when (rejectReason) {
            "TRIP_FULL" -> RejectReason.TripFull
            "TRIP_CANCELLED" -> RejectReason.TripCancelled
            "PICKUP_NOT_POSSIBLE" -> RejectReason.PickupNotPossible
            "OTHER" -> RejectReason.Other
            else -> null
        },
        rejectComment = rejectComment,
    )

    companion object {
        fun fromDomain(booking: Booking): BookingDto = BookingDto(
            id = booking.id,
            tripId = booking.tripId,
            passengerId = booking.passengerId,
            driverId = booking.driverId,
            passengerName = booking.passengerName,
            passengerEmail = booking.passengerEmail,
            originName = booking.originName,
            destinationName = booking.destinationName,
            departureTime = booking.departureTime,
            status = when (booking.status) {
                is BookingStatus.Pending -> "PENDING"
                is BookingStatus.Confirmed -> "CONFIRMED"
                is BookingStatus.Rejected -> "REJECTED"
                is BookingStatus.Cancelled -> "CANCELLED"
            },
            createdAt = booking.createdAt,
            passengerMessage = booking.passengerMessage,
            rejectReason = when (booking.rejectReason) {
                RejectReason.TripFull -> "TRIP_FULL"
                RejectReason.TripCancelled -> "TRIP_CANCELLED"
                RejectReason.PickupNotPossible -> "PICKUP_NOT_POSSIBLE"
                RejectReason.Other -> "OTHER"
                null -> null
            },
            rejectComment = booking.rejectComment,
        )
    }
}
