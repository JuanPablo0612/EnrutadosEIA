package com.juanpablo0612.carpool.domain.chat.repository

import com.juanpablo0612.carpool.domain.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(bookingId: String): Flow<List<Message>>
    suspend fun sendMessage(message: Message): Result<Unit>
    suspend fun markMessagesRead(bookingId: String, userId: String): Result<Unit>
}
