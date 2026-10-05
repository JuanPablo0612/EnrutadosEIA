package com.juanpablo0612.carpool.presentation.notification

import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import com.juanpablo0612.carpool.presentation.ui.util.formatDayMonth
import com.juanpablo0612.carpool.presentation.ui.util.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notification_booking_accepted_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_booking_accepted_title
import enrutadoseia.composeapp.generated.resources.notification_booking_cancelled_by_driver_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_booking_cancelled_by_driver_title
import enrutadoseia.composeapp.generated.resources.notification_booking_cancelled_by_passenger_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_booking_cancelled_by_passenger_title
import enrutadoseia.composeapp.generated.resources.notification_booking_rejected_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_booking_rejected_reason_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_booking_rejected_title
import enrutadoseia.composeapp.generated.resources.notification_generic_body
import enrutadoseia.composeapp.generated.resources.notification_generic_title
import enrutadoseia.composeapp.generated.resources.notification_new_booking_request_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_new_booking_request_title
import enrutadoseia.composeapp.generated.resources.notification_new_message_title_fmt
import enrutadoseia.composeapp.generated.resources.notification_someone
import enrutadoseia.composeapp.generated.resources.notification_trip_cancelled_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_trip_cancelled_title
import enrutadoseia.composeapp.generated.resources.notification_trip_completed_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_trip_completed_title
import enrutadoseia.composeapp.generated.resources.notification_trip_started_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_trip_started_title
import enrutadoseia.composeapp.generated.resources.notification_trip_updated_body_fmt
import enrutadoseia.composeapp.generated.resources.notification_trip_updated_title
import enrutadoseia.composeapp.generated.resources.reject_reason_other
import enrutadoseia.composeapp.generated.resources.reject_reason_pickup_not_possible
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_cancelled
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_full
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Instant

data class NotificationText(val title: String, val body: String)

/**
 * Renders a notification in the device's language from its [type] and [params]. Suspend (uses
 * `getString`, not composition) so the in-app list and the push renderer share it.
 *
 * Kinds this build doesn't know (added by a newer backend) show a generic text.
 */
suspend fun resolveNotificationText(type: NotificationType, params: Map<String, String>): NotificationText {
    suspend fun name(key: String) = params[key]?.takeIf { it.isNotBlank() } ?: getString(Res.string.notification_someone)
    val origin = params[NotificationParams.ORIGIN_NAME].orEmpty()
    val destination = params[NotificationParams.DESTINATION_NAME].orEmpty()
    val departure = formatDeparture(params[NotificationParams.DEPARTURE_TIME])

    suspend fun text(title: StringResource, body: StringResource, who: String) =
        NotificationText(getString(title), getString(body, name(who), origin, destination, departure))

    return when (type) {
        NotificationType.NewBookingRequest -> text(
            Res.string.notification_new_booking_request_title,
            Res.string.notification_new_booking_request_body_fmt,
            NotificationParams.PASSENGER_NAME,
        )
        NotificationType.BookingAccepted -> text(
            Res.string.notification_booking_accepted_title,
            Res.string.notification_booking_accepted_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.BookingRejected -> {
            val reason = rejectReasonText(params[NotificationParams.REJECT_REASON])
            val driver = name(NotificationParams.DRIVER_NAME)
            val body = if (reason == null) {
                getString(Res.string.notification_booking_rejected_body_fmt, driver, origin, destination, departure)
            } else {
                getString(
                    Res.string.notification_booking_rejected_reason_body_fmt,
                    driver, origin, destination, departure, reason,
                )
            }
            NotificationText(getString(Res.string.notification_booking_rejected_title), body)
        }
        NotificationType.BookingCancelledByPassenger -> text(
            Res.string.notification_booking_cancelled_by_passenger_title,
            Res.string.notification_booking_cancelled_by_passenger_body_fmt,
            NotificationParams.PASSENGER_NAME,
        )
        NotificationType.BookingCancelledByDriver -> text(
            Res.string.notification_booking_cancelled_by_driver_title,
            Res.string.notification_booking_cancelled_by_driver_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.TripCancelled -> text(
            Res.string.notification_trip_cancelled_title,
            Res.string.notification_trip_cancelled_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.TripStarted -> text(
            Res.string.notification_trip_started_title,
            Res.string.notification_trip_started_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.TripCompleted -> text(
            Res.string.notification_trip_completed_title,
            Res.string.notification_trip_completed_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.TripUpdated -> text(
            Res.string.notification_trip_updated_title,
            Res.string.notification_trip_updated_body_fmt,
            NotificationParams.DRIVER_NAME,
        )
        NotificationType.NewMessage -> NotificationText(
            getString(Res.string.notification_new_message_title_fmt, name(NotificationParams.SENDER_NAME)),
            params[NotificationParams.MESSAGE_PREVIEW].orEmpty(),
        )
        is NotificationType.Unknown -> NotificationText(
            getString(Res.string.notification_generic_title),
            getString(Res.string.notification_generic_body),
        )
    }
}

/** "dd/MM h:mm AM" in the device's time zone, or "" when missing. */
private suspend fun formatDeparture(epochMs: String?): String {
    val millis = epochMs?.toLongOrNull()?.takeIf { it > 0 } ?: return ""
    val local = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
    val time = formatShortTime(local.hour, local.minute, getString(Res.string.time_am), getString(Res.string.time_pm))
    return "${formatDayMonth(local.date)} $time"
}

private suspend fun rejectReasonText(key: String?): String? = when (key) {
    "TRIP_FULL" -> getString(Res.string.reject_reason_trip_full)
    "TRIP_CANCELLED" -> getString(Res.string.reject_reason_trip_cancelled)
    "PICKUP_NOT_POSSIBLE" -> getString(Res.string.reject_reason_pickup_not_possible)
    "OTHER" -> getString(Res.string.reject_reason_other)
    else -> null
}
