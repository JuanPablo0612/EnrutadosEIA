package com.juanpablo0612.carpool.data.chat.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.chat.datasource.ChatRemoteDataSource
import com.juanpablo0612.carpool.data.chat.model.MessageDto
import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource
) : ChatRepository {

    override fun getMessages(bookingId: String): Flow<List<Message>> {
        return remoteDataSource.getMessages(bookingId)
            .map { list ->
                list.map { it.toDomain() }.sortedBy { it.timestamp }
            }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            val dto = MessageDto.fromDomain(message)
            remoteDataSource.sendMessage(message.bookingId, message.id, dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.ChatException.Unknown)
        }
    }

    override suspend fun markMessagesRead(bookingId: String, userId: String): Result<Unit> {
        return try {
            remoteDataSource.markMessagesRead(bookingId, userId)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.ChatException.Unknown)
        }
    }
}
