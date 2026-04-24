package com.juanpablo0612.carpool.data.booking.model

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val id: String = "",
    val routeId: String = "",
    val vehicleId: String = "",
    val passengerId: String = "",
    val driverId: String = "",
    val passengerName: String = "",
    val passengerEmail: String = "",
    val status: String = "PENDING",
    val createdAt: Long = 0L
) {
    fun toDomain(): Booking = Booking(
        id = id,
        routeId = routeId,
        vehicleId = vehicleId,
        passengerId = passengerId,
        driverId = driverId,
        passengerName = passengerName,
        passengerEmail = passengerEmail,
        status = when (status) {
            "CONFIRMED" -> BookingStatus.Confirmed
            "REJECTED" -> BookingStatus.Rejected
            "CANCELLED" -> BookingStatus.Cancelled
            else -> BookingStatus.Pending
        },
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(booking: Booking): BookingDto = BookingDto(
            id = booking.id,
            routeId = booking.routeId,
            vehicleId = booking.vehicleId,
            passengerId = booking.passengerId,
            driverId = booking.driverId,
            passengerName = booking.passengerName,
            passengerEmail = booking.passengerEmail,
            status = when (booking.status) {
                is BookingStatus.Pending -> "PENDING"
                is BookingStatus.Confirmed -> "CONFIRMED"
                is BookingStatus.Rejected -> "REJECTED"
                is BookingStatus.Cancelled -> "CANCELLED"
            },
            createdAt = booking.createdAt
        )
    }
}
