package com.juanpablo0612.carpool.presentation.chat

import com.juanpablo0612.carpool.domain.chat.model.Message

/** Where one message stands from this device's point of view. */
sealed class MessageStatus {
    /** In the conversation as stored. */
    data object Sent : MessageStatus()

    /** Written here and on its way; shown at once so typing never waits on the network. */
    data object Sending : MessageStatus()

    /** The last attempt failed; tapping the bubble sends the same message again. */
    data object Failed : MessageStatus()
}

data class ChatMessageItem(val message: Message, val status: MessageStatus)

/**
 * The conversation as shown: the stored [confirmed] messages plus this device's [outgoing] ones
 * still sending or failed. An outgoing copy wins over a stored one with the same id, since the
 * local database lists a pending write before the server has accepted it.
 */
internal fun mergeChatMessages(confirmed: List<Message>, outgoing: List<ChatMessageItem>): List<ChatMessageItem> {
    if (outgoing.isEmpty()) return confirmed.map { ChatMessageItem(it, MessageStatus.Sent) }
    val outgoingIds = outgoing.mapTo(HashSet()) { it.message.id }
    return (confirmed.filter { it.id !in outgoingIds }.map { ChatMessageItem(it, MessageStatus.Sent) } + outgoing)
        .sortedBy { it.message.timestamp }
}
