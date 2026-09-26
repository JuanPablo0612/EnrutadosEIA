package com.juanpablo0612.carpool.domain.notification.model

/**
 * Notification kinds, keyed as the backend writes them (functions/src/model.ts). Each kind is
 * rendered into localized text and a deep link on the device.
 */
sealed class NotificationType(val key: String) {
    data object NewBookingRequest : NotificationType("new_booking_request")
    data object BookingAccepted : NotificationType("booking_accepted")
    data object BookingRejected : NotificationType("booking_rejected")
    data object BookingCancelledByPassenger : NotificationType("booking_cancelled_by_passenger")
    data object BookingCancelledByDriver : NotificationType("booking_cancelled_by_driver")
    data object TripCancelled : NotificationType("trip_cancelled")
    data object TripStarted : NotificationType("trip_started")
    data object TripCompleted : NotificationType("trip_completed")
    data object NewMessage : NotificationType("new_message")

    /** A kind this build doesn't know (written by a newer backend); shown generically. */
    data class Unknown(val rawKey: String) : NotificationType(rawKey)

    companion object {
        // Lazy: the subclass objects are not initialized yet while this companion is.
        private val known by lazy {
            listOf(
                NewBookingRequest, BookingAccepted, BookingRejected, BookingCancelledByPassenger,
                BookingCancelledByDriver, TripCancelled, TripStarted, TripCompleted, NewMessage,
            )
        }

        fun fromKey(key: String): NotificationType = known.firstOrNull { it.key == key } ?: Unknown(key)
    }
}
