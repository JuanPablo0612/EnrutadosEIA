package com.juanpablo0612.carpool.data.notification.model

import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType

/** A push as the backend sends it (functions/src/notifications/notify.ts). */
internal data class PushPayload(
    val type: NotificationType,
    val recipientId: String,
    val notificationId: String,
    val params: Map<String, String>,
) {
    /**
     * Whether this device should show the push now. A shared device only shows pushes meant for
     * whoever is signed in, and a chat message is not shown on top of that same chat.
     */
    fun shouldShow(signedInUserId: String?, openChatBookingId: String?): Boolean {
        if (recipientId != signedInUserId) return false
        return !(type == NotificationType.NewMessage &&
            params[NotificationParams.BOOKING_ID] == openChatBookingId)
    }

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
