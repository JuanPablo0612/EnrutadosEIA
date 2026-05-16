package com.juanpablo0612.carpool.presentation.chat

sealed class ChatAction {
    data class OnInputChange(val text: String) : ChatAction()
    data class OnQuickReplyClick(val text: String) : ChatAction()
    data object OnSendClick : ChatAction()
    data object OnBackClick : ChatAction()
}
