package com.juanpablo0612.carpool.domain.chat.usecase

import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import kotlin.time.Clock

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(
        bookingId: String,
        senderId: String,
        text: String
    ): Result<Unit> {
        val message = Message(
            id = "${Clock.System.now().toEpochMilliseconds()}_$senderId",
            bookingId = bookingId,
            senderId = senderId,
            text = text.trim(),
            timestamp = Clock.System.now().toEpochMilliseconds(),
            isRead = false
        )
        return repository.sendMessage(message)
    }
}
