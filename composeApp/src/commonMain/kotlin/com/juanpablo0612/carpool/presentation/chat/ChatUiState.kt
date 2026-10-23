package com.juanpablo0612.carpool.presentation.chat

import com.juanpablo0612.carpool.domain.chat.model.Message

data class ChatUiState(
    /** The conversation as stored. */
    val messages: List<Message> = emptyList(),
    /** This device's messages still sending or that failed, until the send succeeds. */
    val outgoing: List<ChatMessageItem> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val isReadOnly: Boolean = false,
    val otherPartyName: String = "",
    val currentUserId: String = "",
    val error: ChatError? = null
) {
    val items: List<ChatMessageItem>
        get() = mergeChatMessages(messages, outgoing)
}
