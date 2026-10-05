package com.juanpablo0612.carpool.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.juanpablo0612.carpool.R
import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import com.juanpablo0612.carpool.presentation.navigation.NotificationDeepLink
import com.juanpablo0612.carpool.presentation.notification.resolveNotificationText
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notification_channel_bookings_description
import enrutadoseia.composeapp.generated.resources.notification_channel_bookings_name
import enrutadoseia.composeapp.generated.resources.notification_channel_chat_description
import enrutadoseia.composeapp.generated.resources.notification_channel_chat_name
import enrutadoseia.composeapp.generated.resources.notification_channel_trips_description
import enrutadoseia.composeapp.generated.resources.notification_channel_trips_name
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/** A push as the backend sends it (functions/src/notifications/notify.ts). */
internal data class PushPayload(
    val type: NotificationType,
    val recipientId: String,
    val notificationId: String,
    val params: Map<String, String>,
) {
    companion object {
        private val ENVELOPE_KEYS = setOf("v", "type", "recipientId", "notificationId", "sentAt")

        fun from(data: Map<String, String>): PushPayload? {
            if (data["v"] == null) return null
            val type = data["type"]?.takeIf { it.isNotBlank() } ?: return null
            val recipientId = data["recipientId"]?.takeIf { it.isNotBlank() } ?: return null
            return PushPayload(
                type = NotificationType.fromKey(type),
                recipientId = recipientId,
                notificationId = data["notificationId"].orEmpty(),
                params = data - ENVELOPE_KEYS,
            )
        }
    }
}

/** Shows a push as a system notification, rendered in the device's language. */
internal object PushNotificationRenderer {

    private sealed class Channel(val id: String, val name: StringResource, val description: StringResource) {
        data object Bookings : Channel(
            "bookings", Res.string.notification_channel_bookings_name, Res.string.notification_channel_bookings_description,
        )
        data object Trips : Channel(
            "trips", Res.string.notification_channel_trips_name, Res.string.notification_channel_trips_description,
        )
        data object Chat : Channel(
            "chat", Res.string.notification_channel_chat_name, Res.string.notification_channel_chat_description,
        )
    }

    suspend fun show(context: Context, payload: PushPayload) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled() || !hasPostPermission(context)) return

        val channel = channelFor(payload.type)
        ensureChannel(context, channel)

        val text = resolveNotificationText(payload.type, payload.params)
        val tag = if (payload.type == NotificationType.NewMessage) {
            // One notification per chat, replaced by each new message.
            "chat:${payload.params[NotificationParams.BOOKING_ID].orEmpty()}"
        } else {
            payload.notificationId.ifBlank { "${payload.type.key}:${System.currentTimeMillis()}" }
        }

        val builder = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(text.title)
            .setContentText(text.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent(context, payload, tag))

        if (channel == Channel.Chat) {
            // Keep message previews off the lock screen.
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPublicVersion(
                    NotificationCompat.Builder(context, channel.id)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(getString(Channel.Chat.name))
                        .build()
                )
        }

        @Suppress("MissingPermission") // checked by hasPostPermission above
        manager.notify(tag, 0, builder.build())
    }

    private fun channelFor(type: NotificationType): Channel = when (type) {
        NotificationType.NewMessage -> Channel.Chat
        NotificationType.TripStarted, NotificationType.TripCompleted, NotificationType.TripCancelled,
        NotificationType.TripUpdated -> Channel.Trips
        else -> Channel.Bookings
    }

    private suspend fun ensureChannel(context: Context, channel: Channel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        // Re-creating an existing channel only refreshes its localized name and description.
        manager.createNotificationChannel(
            NotificationChannel(channel.id, getString(channel.name), NotificationManager.IMPORTANCE_HIGH).apply {
                description = getString(channel.description)
            }
        )
    }

    private fun hasPostPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun contentIntent(context: Context, payload: PushPayload, tag: String): PendingIntent? {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        NotificationDeepLink.forNotification(payload.type, payload.params)?.let {
            launch.putExtra(PushIntentExtras.DEEP_LINK, it)
        }
        launch.putExtra(PushIntentExtras.NOTIFICATION_ID, payload.notificationId)
        launch.putExtra(PushIntentExtras.RECIPIENT_ID, payload.recipientId)
        return PendingIntent.getActivity(
            context,
            tag.hashCode(),
            launch,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
