package com.juanpablo0612.carpool.domain.chat.usecase

import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository

/**
 * Sending is split in two so a message keeps one identity from the moment it is typed: the
 * caller builds it with [createMessage], can show it right away, and passes the same message to
 * [invoke] for the first attempt and every retry. The repository writes it under its id, so a
 * retry after an unclear failure can never duplicate it.
 */
class SendMessageUseCase(private val repository: ChatRepository) {

    fun createMessage(bookingId: String, senderId: String, text: String, now: Long): Message = Message(
        id = "${now}_$senderId",
        bookingId = bookingId,
        senderId = senderId,
        text = text.trim(),
        timestamp = now,
        isRead = false,
    )

    suspend operator fun invoke(message: Message): Result<Unit> = repository.sendMessage(message)
}
