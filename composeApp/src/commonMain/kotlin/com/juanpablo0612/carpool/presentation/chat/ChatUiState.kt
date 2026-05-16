package com.juanpablo0612.carpool.presentation.chat

import com.juanpablo0612.carpool.domain.chat.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val isReadOnly: Boolean = false,
    val otherPartyName: String = "",
    val currentUserId: String = ""
)
