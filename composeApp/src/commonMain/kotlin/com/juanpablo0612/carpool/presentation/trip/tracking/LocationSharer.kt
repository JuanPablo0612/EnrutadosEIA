package com.juanpablo0612.carpool.presentation.trip.tracking

/**
 * Opens the platform's SMS composer pre-addressed to [phoneNumbers] and pre-filled with
 * [message], so the user reviews and sends it with one tap.
 *
 * This is the mechanism behind SOS's "share live location": `EmergencyContact` (domain/safety)
 * only stores a phone number (no linked in-app user id), so an in-app notification isn't possible,
 * and silently auto-sending an SMS would require the sensitive SEND_SMS runtime permission — not
 * requested anywhere else in this app. Opening the composer needs no permission and keeps the user
 * in control of what actually gets sent.
 */
interface LocationSharer {
    fun share(phoneNumbers: List<String>, message: String)
}

/**
 * Creates the platform [LocationSharer]. See [createEmergencyDialer] for the `context` seam.
 */
expect fun createLocationSharer(context: Any? = null): LocationSharer
