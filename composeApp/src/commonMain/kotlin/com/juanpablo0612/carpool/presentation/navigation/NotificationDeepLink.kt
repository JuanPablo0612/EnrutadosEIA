package com.juanpablo0612.carpool.presentation.navigation

import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType

/**
 * The deep-link vocabulary for notifications, as plain text so it can travel through a push
 * tap intent. [forNotification] builds the link for a notification from
 * its type and params; the nav host resolves a link back to a typed destination with
 * [toRouteOrNull]. Keeping both directions in one file stops the two halves from drifting.
 *
 * An unrecognised or malformed link resolves to `null` and the tap simply marks the notification
 * read — a notification kind added by a newer backend should never crash navigation.
 */
object NotificationDeepLink {
    private const val SCHEME = "carpool://"

    fun passengerBookings(): String = "${SCHEME}passenger-bookings"
    fun bookingRequests(): String = "${SCHEME}booking-requests"
    fun trip(tripId: String): String = "${SCHEME}trip/$tripId"
    fun tracking(tripId: String): String = "${SCHEME}tracking/$tripId"
    fun tripPassengers(tripId: String): String = "${SCHEME}trip-passengers/$tripId"

    /** The other party's name travels in the link because it is [Route.Chat]'s title. */
    fun chat(bookingId: String, tripId: String, otherPartyName: String): String =
        "${SCHEME}chat/$bookingId/$tripId/${encodeSegment(otherPartyName)}"

    /** Rating the driver of a completed trip. */
    fun rateDriver(bookingId: String, tripId: String, driverId: String, driverName: String): String =
        "${SCHEME}rate/$bookingId/$tripId/$driverId/${encodeSegment(driverName)}"

    /** Where tapping a notification of [type] should go, or `null` if it has nowhere to go. */
    fun forNotification(type: NotificationType, params: Map<String, String>): String? {
        fun param(key: String) = params[key]?.takeIf { it.isNotBlank() }
        val tripId = param(NotificationParams.TRIP_ID)
        val bookingId = param(NotificationParams.BOOKING_ID)
        return when (type) {
            NotificationType.NewBookingRequest -> bookingRequests()
            NotificationType.BookingAccepted,
            NotificationType.BookingRejected,
            NotificationType.BookingCancelledByDriver,
            NotificationType.TripCancelled -> passengerBookings()
            NotificationType.BookingCancelledByPassenger -> tripId?.let(::tripPassengers)
            NotificationType.TripStarted -> tripId?.let(::tracking)
            NotificationType.TripCompleted -> {
                val driverId = param(NotificationParams.DRIVER_ID)
                if (bookingId != null && tripId != null && driverId != null) {
                    rateDriver(bookingId, tripId, driverId, params[NotificationParams.DRIVER_NAME].orEmpty())
                } else {
                    passengerBookings()
                }
            }
            NotificationType.NewMessage ->
                if (bookingId != null && tripId != null) {
                    chat(bookingId, tripId, params[NotificationParams.SENDER_NAME].orEmpty())
                } else null
            is NotificationType.Unknown -> null
        }
    }

    internal fun parse(link: String): Route? {
        if (!link.startsWith(SCHEME)) return null
        val path = link.removePrefix(SCHEME).trim('/')
        if (path.isEmpty()) return null

        val segments = path.split('/')
        fun arg(index: Int) = segments.getOrNull(index)?.takeIf { it.isNotBlank() }

        return when (segments.first()) {
            "passenger-bookings" -> Route.MyTrips
            "booking-requests" -> Route.DriverBookingRequests
            "trip" -> arg(1)?.let { Route.TripDetailPassenger(it) }
            "tracking" -> arg(1)?.let { Route.TripTracking(it) }
            "trip-passengers" -> arg(1)?.let { Route.TripPassengers(it) }
            "chat" -> {
                val bookingId = arg(1) ?: return null
                val tripId = arg(2) ?: return null
                // The chat re-derives read-only from the trip's live status.
                Route.Chat(bookingId, tripId, decodeSegment(segments.getOrNull(3).orEmpty()), isReadOnly = false)
            }
            "rate" -> {
                val bookingId = arg(1) ?: return null
                val tripId = arg(2) ?: return null
                val driverId = arg(3) ?: return null
                Route.PostTripRating(
                    bookingId = bookingId,
                    tripId = tripId,
                    rateeId = driverId,
                    rateeName = decodeSegment(segments.getOrNull(4).orEmpty()),
                    rateeIsDriver = true,
                )
            }
            else -> null
        }
    }

    private const val UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"

    /** Percent-encodes a free-text path segment (names may contain spaces, slashes, accents). */
    private fun encodeSegment(value: String): String = buildString {
        value.encodeToByteArray().forEach { byte ->
            val char = byte.toInt().toChar()
            if (byte >= 0 && char in UNRESERVED) {
                append(char)
            } else {
                append('%')
                append((byte.toInt() and 0xFF).toString(16).uppercase().padStart(2, '0'))
            }
        }
    }

    private fun decodeSegment(value: String): String {
        val bytes = mutableListOf<Byte>()
        var i = 0
        while (i < value.length) {
            val hex = if (value[i] == '%' && i + 2 <= value.lastIndex) {
                value.substring(i + 1, i + 3).toIntOrNull(16)
            } else null
            if (hex != null) {
                bytes += hex.toByte()
                i += 3
            } else {
                bytes += value[i].toString().encodeToByteArray().toList()
                i++
            }
        }
        return bytes.toByteArray().decodeToString()
    }
}

/** Resolves a notification deep link to a destination, or `null` if unrecognised. */
fun String.toRouteOrNull(): Route? = NotificationDeepLink.parse(this)
