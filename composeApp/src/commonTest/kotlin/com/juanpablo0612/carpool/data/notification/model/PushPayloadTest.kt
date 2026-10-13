package com.juanpablo0612.carpool.data.notification.model

import com.juanpablo0612.carpool.domain.notification.model.NotificationParams
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PushPayloadTest {

    private val envelope = mapOf(
        "v" to "1",
        "type" to "booking_accepted",
        "recipientId" to "u1",
        "notificationId" to "n1",
        "sentAt" to "1700000000000",
    )

    @Test
    fun parsesTheEnvelopeAndKeepsOnlyRenderParams() {
        val payload = PushPayload.from(envelope + (NotificationParams.DRIVER_NAME to "Laura"))

        assertEquals(
            PushPayload(
                type = NotificationType.BookingAccepted,
                recipientId = "u1",
                notificationId = "n1",
                params = mapOf(NotificationParams.DRIVER_NAME to "Laura"),
            ),
            payload,
        )
    }

    @Test
    fun ignoresDataWithoutTheBackendVersion() {
        assertNull(PushPayload.from(envelope - "v"))
    }

    @Test
    fun ignoresDataWithoutTypeOrRecipient() {
        assertNull(PushPayload.from(envelope + ("type" to " ")))
        assertNull(PushPayload.from(envelope - "recipientId"))
    }

    @Test
    fun aMissingNotificationIdBecomesBlank() {
        assertEquals("", PushPayload.from(envelope - "notificationId")?.notificationId)
    }

    @Test
    fun showsOnlyToTheSignedInRecipient() {
        val payload = requireNotNull(PushPayload.from(envelope))

        assertTrue(payload.shouldShow(signedInUserId = "u1", openChatBookingId = null))
        assertFalse(payload.shouldShow(signedInUserId = "u2", openChatBookingId = null))
        assertFalse(payload.shouldShow(signedInUserId = null, openChatBookingId = null))
    }

    @Test
    fun hidesAMessageForTheChatOnScreenOnly() {
        val message = requireNotNull(
            PushPayload.from(envelope + ("type" to "new_message") + (NotificationParams.BOOKING_ID to "b1"))
        )

        assertFalse(message.shouldShow(signedInUserId = "u1", openChatBookingId = "b1"))
        assertTrue(message.shouldShow(signedInUserId = "u1", openChatBookingId = "b2"))
        assertTrue(message.shouldShow(signedInUserId = "u1", openChatBookingId = null))
    }
}
