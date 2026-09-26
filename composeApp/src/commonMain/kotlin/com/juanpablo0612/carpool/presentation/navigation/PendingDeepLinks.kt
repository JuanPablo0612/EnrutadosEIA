package com.juanpablo0612.carpool.presentation.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A tapped push notification waiting for the nav host to open it. */
data class PendingDeepLink(
    val deepLink: String,
    /** The in-app notification to mark as read, if the push had one. */
    val notificationId: String?,
    /** Who the push was for; ignored if someone else is signed in by the time it is handled. */
    val recipientId: String,
)

/**
 * Hands a push tap from the Activity to the nav host. Held until the app reaches a signed-in
 * destination, so a cold start's Splash navigation can't wipe the target.
 */
class PendingDeepLinks {
    private val _link = MutableStateFlow<PendingDeepLink?>(null)
    val link: StateFlow<PendingDeepLink?> = _link.asStateFlow()

    fun offer(link: PendingDeepLink) {
        _link.value = link
    }

    fun consume() {
        _link.value = null
    }
}
