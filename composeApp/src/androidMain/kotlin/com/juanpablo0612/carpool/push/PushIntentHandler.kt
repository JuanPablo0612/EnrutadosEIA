package com.juanpablo0612.carpool.push

import android.content.Intent
import com.juanpablo0612.carpool.presentation.navigation.PendingDeepLink
import com.juanpablo0612.carpool.presentation.navigation.PendingDeepLinks
import org.koin.mp.KoinPlatform

/** Extras a push notification's tap intent carries into MainActivity. */
internal object PushIntentExtras {
    const val DEEP_LINK = "com.juanpablo0612.carpool.push.DEEP_LINK"
    const val NOTIFICATION_ID = "com.juanpablo0612.carpool.push.NOTIFICATION_ID"
    const val RECIPIENT_ID = "com.juanpablo0612.carpool.push.RECIPIENT_ID"
}

/** Forwards a push tap from MainActivity's intent to the nav host. */
object PushIntentHandler {
    fun handle(intent: Intent?) {
        val deepLink = intent?.getStringExtra(PushIntentExtras.DEEP_LINK) ?: return
        val recipientId = intent.getStringExtra(PushIntentExtras.RECIPIENT_ID) ?: return
        KoinPlatform.getKoin().get<PendingDeepLinks>().offer(
            PendingDeepLink(
                deepLink = deepLink,
                notificationId = intent.getStringExtra(PushIntentExtras.NOTIFICATION_ID)?.ifBlank { null },
                recipientId = recipientId,
            )
        )
        // Consumed: a configuration change re-delivering this intent must not navigate again.
        intent.removeExtra(PushIntentExtras.DEEP_LINK)
        intent.removeExtra(PushIntentExtras.NOTIFICATION_ID)
        intent.removeExtra(PushIntentExtras.RECIPIENT_ID)
    }
}
