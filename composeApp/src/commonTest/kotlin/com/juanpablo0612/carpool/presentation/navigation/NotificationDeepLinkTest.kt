package com.juanpablo0612.carpool.presentation.navigation

import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationDeepLinkTest {

    @Test
    fun chatLinkRoundTripsNamesWithSpacesSlashesAndAccents() {
        val link = NotificationDeepLink.chat("b1", "t1", "José / María Núñez")
        assertEquals(Route.Chat("b1", "t1", "José / María Núñez", isReadOnly = false), link.toRouteOrNull())
    }

    @Test
    fun completedTripLinksToRatingTheDriver() {
        val params = mapOf(
            NotificationParams.BOOKING_ID to "b1",
            NotificationParams.TRIP_ID to "t1",
            NotificationParams.DRIVER_ID to "d1",
            NotificationParams.DRIVER_NAME to "Laura Gómez",
            NotificationParams.DEPARTURE_TIME to "1700000000000",
            NotificationParams.ORIGIN_NAME to "Viva Envigado",
            NotificationParams.DESTINATION_NAME to "EIA / Las Palmas",
        )
        val route = NotificationDeepLink.forNotification(NotificationType.TripCompleted, params)?.toRouteOrNull()
        assertEquals(
            Route.PostTripRating(
                bookingId = "b1",
                tripId = "t1",
                rateeId = "d1",
                rateeName = "Laura Gómez",
                rateeIsDriver = true,
                departureTime = 1_700_000_000_000,
                originName = "Viva Envigado",
                destinationName = "EIA / Las Palmas",
            ),
            route,
        )
    }

    @Test
    fun aRatingLinkWithoutTheTripStillOpens() {
        val params = mapOf(
            NotificationParams.BOOKING_ID to "b1",
            NotificationParams.TRIP_ID to "t1",
            NotificationParams.DRIVER_ID to "d1",
        )
        val route = NotificationDeepLink.forNotification(NotificationType.TripCompleted, params)?.toRouteOrNull()
        assertEquals(Route.PostTripRating("b1", "t1", "d1", "", rateeIsDriver = true), route)
    }

    @Test
    fun bookingUpdatesOpenMyTrips() {
        val route = NotificationDeepLink.forNotification(NotificationType.BookingAccepted, emptyMap())?.toRouteOrNull()
        assertEquals(Route.MyTrips, route)
    }

    @Test
    fun passengerCancellationOpensTheTripPassengers() {
        val params = mapOf(NotificationParams.TRIP_ID to "t1")
        val route = NotificationDeepLink.forNotification(NotificationType.BookingCancelledByPassenger, params)
            ?.toRouteOrNull()
        assertEquals(Route.TripPassengers("t1"), route)
    }

    @Test
    fun unknownKindsAndMalformedLinksGoNowhere() {
        assertNull(NotificationDeepLink.forNotification(NotificationType.Unknown("future"), emptyMap()))
        assertNull("carpool://chat/b1".toRouteOrNull())
        assertNull("https://example.com".toRouteOrNull())
        assertNull("carpool://".toRouteOrNull())
    }

    @Test
    fun unknownTypeKeysDecodeAsUnknown() {
        assertEquals(NotificationType.Unknown("x"), NotificationType.fromKey("x"))
        assertEquals(NotificationType.TripStarted, NotificationType.fromKey("trip_started"))
    }
}
