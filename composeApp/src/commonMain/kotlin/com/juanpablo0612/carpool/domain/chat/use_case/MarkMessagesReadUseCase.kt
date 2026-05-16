package com.juanpablo0612.carpool.domain.chat.use_case

import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository

class MarkMessagesReadUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(bookingId: String, userId: String): Result<Unit> =
        repository.markMessagesRead(bookingId, userId)
}
