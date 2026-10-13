package com.juanpablo0612.carpool.push

import com.juanpablo0612.carpool.data.notification.datasource.PushTokenSync
import com.juanpablo0612.carpool.data.notification.model.PushPayload
import com.juanpablo0612.carpool.presentation.chat.ActiveChatRegistry
import com.juanpablo0612.carpool.presentation.navigation.NotificationDeepLink
import com.juanpablo0612.carpool.presentation.navigation.PendingDeepLink
import com.juanpablo0612.carpool.presentation.navigation.PendingDeepLinks
import dev.gitlive.firebase.auth.FirebaseAuth
import org.koin.mp.KoinPlatform

/**
 * The app side of push on iOS, called from the Swift notification delegates
 * (iosApp/iosApp/AppDelegate+Notifications.swift). The text itself is rendered before delivery by
 * the NotificationService extension, so this only decides about showing and opening pushes.
 */
object IosPushNotifications {

    /** Re-registers after FCM rotates this device's token. */
    fun onNewToken() {
        KoinPlatform.getKoin().get<PushTokenSync>().onNewToken()
    }

    /** Whether a push that arrives while the app is open is shown as a banner. */
    fun shouldPresentInForeground(userInfo: Map<Any?, *>): Boolean {
        // Not one of the backend's pushes (e.g. a Firebase console message): let the system show it.
        val payload = PushPayload.from(userInfo.stringValues()) ?: return true
        val signedInUserId = KoinPlatform.getKoin().get<FirebaseAuth>().currentUser?.uid
        return payload.shouldShow(signedInUserId, ActiveChatRegistry.currentBookingId)
    }

    /** Forwards a push tap to the nav host, as PushIntentHandler does on Android. */
    fun onNotificationTapped(userInfo: Map<Any?, *>) {
        val payload = PushPayload.from(userInfo.stringValues()) ?: return
        val deepLink = NotificationDeepLink.forNotification(payload.type, payload.params) ?: return
        KoinPlatform.getKoin().get<PendingDeepLinks>().offer(
            PendingDeepLink(
                deepLink = deepLink,
                notificationId = payload.notificationId.ifBlank { null },
                recipientId = payload.recipientId,
            )
        )
    }

    /** FCM puts the data map at the top level of the APNs payload, next to the `aps` dictionary. */
    private fun Map<Any?, *>.stringValues(): Map<String, String> = buildMap {
        this@stringValues.forEach { (key, value) ->
            if (key is String && value is String) put(key, value)
        }
    }
}
