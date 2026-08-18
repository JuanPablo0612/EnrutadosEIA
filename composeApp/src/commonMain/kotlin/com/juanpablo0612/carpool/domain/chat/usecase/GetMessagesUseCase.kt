package com.juanpablo0612.carpool.domain.chat.usecase

import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(bookingId: String): Flow<List<Message>> =
        repository.getMessages(bookingId)
}
