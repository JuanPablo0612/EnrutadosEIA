package com.juanpablo0612.carpool.domain.notification.model

/**
 * Keys of the string params the backend attaches to a notification (and to its push payload).
 * Mirrors the params built in functions/src/notifications and functions/src/triggers.
 */
object NotificationParams {
    const val BOOKING_ID = "bookingId"
    const val TRIP_ID = "tripId"
    const val ORIGIN_NAME = "originName"
    const val DESTINATION_NAME = "destinationName"
    /** Epoch milliseconds, as a decimal string. */
    const val DEPARTURE_TIME = "departureTime"
    const val DRIVER_ID = "driverId"
    const val DRIVER_NAME = "driverName"
    const val PASSENGER_NAME = "passengerName"
    const val REJECT_REASON = "rejectReason"
    const val SENDER_ID = "senderId"
    const val SENDER_NAME = "senderName"
    const val MESSAGE_PREVIEW = "messagePreview"
}
