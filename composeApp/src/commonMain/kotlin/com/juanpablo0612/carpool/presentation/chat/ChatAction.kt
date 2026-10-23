package com.juanpablo0612.carpool.presentation.chat

sealed class ChatAction {
    data class OnInputChange(val text: String) : ChatAction()
    data class OnQuickReplyClick(val text: String) : ChatAction()
    data object OnSendClick : ChatAction()

    /** A failed message tapped: send it again as is. */
    data class OnRetryMessage(val messageId: String) : ChatAction()
    data object OnBackClick : ChatAction()
    data object OnRetryLoad : ChatAction()
}
