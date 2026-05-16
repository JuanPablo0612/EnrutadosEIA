package com.juanpablo0612.carpool.presentation.chat

sealed class ChatEvent {
    data object NavigateBack : ChatEvent()
}
