package com.juanpablo0612.carpool.presentation.navigation

/**
 * The deep-link vocabulary carried by `AppNotification.deepLink`.
 *
 * Notifications are persisted to Firestore, so the link has to survive as plain text rather than
 * as a serialized [Route]. Producers build one with the helpers here; the nav host resolves it
 * back to a typed destination with [toRouteOrNull]. Keeping both directions in one file is what
 * stops the two halves from drifting.
 *
 * An unrecognised or malformed link resolves to `null` and the tap simply marks the notification
 * read — a notification written by an older or newer build should never crash navigation.
 */
object NotificationDeepLink {
    private const val SCHEME = "carpool://"

    fun passengerBookings(): String = "${SCHEME}passenger-bookings"
    fun bookingRequests(): String = "${SCHEME}booking-requests"
    fun trip(tripId: String): String = "${SCHEME}trip/$tripId"
    fun tracking(tripId: String): String = "${SCHEME}tracking/$tripId"
    fun chat(bookingId: String): String = "${SCHEME}chat/$bookingId"

    internal fun parse(link: String): Route? {
        if (!link.startsWith(SCHEME)) return null
        val path = link.removePrefix(SCHEME).trim('/')
        if (path.isEmpty()) return null

        val segments = path.split('/')
        val head = segments.first()
        val arg = segments.getOrNull(1)?.takeIf { it.isNotBlank() }

        return when (head) {
            "passenger-bookings" -> Route.PassengerBookings
            "booking-requests" -> Route.DriverBookingRequests
            "trip" -> arg?.let { Route.TripDetailPassenger(it) }
            "tracking" -> arg?.let { Route.TripTracking(it) }
            "chat" -> arg?.let { Route.Chat(it) }
            else -> null
        }
    }
}

/** Resolves a persisted notification deep link to a destination, or `null` if unrecognised. */
fun String.toRouteOrNull(): Route? = NotificationDeepLink.parse(this)
