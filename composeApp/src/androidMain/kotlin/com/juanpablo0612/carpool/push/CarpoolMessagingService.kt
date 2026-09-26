package com.juanpablo0612.carpool.push

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenSync
import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import com.juanpablo0612.carpool.presentation.chat.ActiveChatRegistry
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform

/**
 * Receives the backend's data-only pushes and shows them as system notifications. Rendering
 * happens here, not on the server, so the text follows the device's language.
 */
class CarpoolMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        KoinPlatform.getKoin().get<PushTokenSync>().onNewToken()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val payload = PushPayload.from(message.data) ?: return
        // A shared device only shows pushes meant for whoever is signed in now.
        if (payload.recipientId != FirebaseAuth.getInstance().currentUser?.uid) return
        if (payload.type == NotificationType.NewMessage &&
            payload.params[NotificationParams.BOOKING_ID] == ActiveChatRegistry.currentBookingId
        ) return

        // onMessageReceived runs on a background thread with a short time budget; rendering only
        // resolves localized strings, so blocking here is fine.
        runBlocking { PushNotificationRenderer.show(applicationContext, payload) }
    }
}
