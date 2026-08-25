package com.juanpablo0612.carpool.data.chat.datasource

import com.juanpablo0612.carpool.data.chat.model.MessageDto
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    fun getMessages(bookingId: String): Flow<List<MessageDto>>
    suspend fun sendMessage(bookingId: String, messageId: String, message: MessageDto)
    suspend fun markMessagesRead(bookingId: String, userId: String)
}
