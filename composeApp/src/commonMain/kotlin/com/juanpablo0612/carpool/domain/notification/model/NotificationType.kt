package com.juanpablo0612.carpool.domain.notification.model

sealed class NotificationType(val key: String) {
    data object BookingAccepted : NotificationType("booking_accepted")
    data object BookingRejected : NotificationType("booking_rejected")
    data object NewBookingRequest : NotificationType("new_booking_request")
    data object TripStarted : NotificationType("trip_started")
    data object TripCompleted : NotificationType("trip_completed")
    data object NewMessage : NotificationType("new_message")

    companion object {
        fun fromKey(key: String): NotificationType = when (key) {
            "booking_accepted" -> BookingAccepted
            "booking_rejected" -> BookingRejected
            "new_booking_request" -> NewBookingRequest
            "trip_started" -> TripStarted
            "trip_completed" -> TripCompleted
            "new_message" -> NewMessage
            else -> TripCompleted
        }
    }
}
