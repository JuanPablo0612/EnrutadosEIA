package com.juanpablo0612.carpool.presentation.chat

import kotlin.concurrent.Volatile

/**
 * The chat currently on screen, so a push for a message in that same chat is not shown on top of
 * it. Written from the UI, read from the push service's thread.
 */
object ActiveChatRegistry {
    @Volatile
    var currentBookingId: String? = null
        internal set
}
