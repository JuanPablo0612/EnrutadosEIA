package com.juanpablo0612.carpool.presentation.chat

import com.juanpablo0612.carpool.domain.chat.model.Message
import kotlin.test.Test
import kotlin.test.assertEquals

class MergeChatMessagesTest {

    private fun message(id: String, timestamp: Long) =
        Message(id = id, bookingId = "b1", senderId = "u1", text = id, timestamp = timestamp, isRead = false)

    @Test
    fun storedMessagesAreSent() {
        val stored = listOf(message("a", 1), message("b", 2))

        assertEquals(
            stored.map { ChatMessageItem(it, MessageStatus.Sent) },
            mergeChatMessages(stored, emptyList()),
        )
    }

    @Test
    fun outgoingMessagesJoinInTimeOrder() {
        val stored = listOf(message("a", 1), message("c", 3))
        val pending = ChatMessageItem(message("b", 2), MessageStatus.Sending)

        assertEquals(listOf("a", "b", "c"), mergeChatMessages(stored, listOf(pending)).map { it.message.id })
    }

    @Test
    fun outgoingStatusWinsOverAStoredCopyWithTheSameId() {
        val failed = ChatMessageItem(message("a", 1), MessageStatus.Failed)

        assertEquals(listOf(failed), mergeChatMessages(listOf(message("a", 1)), listOf(failed)))
    }
}
