package com.juanpablo0612.carpool.data.chat

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(private val firestore: FirebaseFirestore) : ChatRepository {

    override fun getMessages(bookingId: String): Flow<List<Message>> {
        return firestore.collection(CHATS_COLLECTION)
            .document(bookingId)
            .collection(MESSAGES_COLLECTION)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(MessageDto.serializer()).toDomain() }
                    .sortedBy { it.timestamp }
            }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            val dto = MessageDto.fromDomain(message)
            firestore.collection(CHATS_COLLECTION)
                .document(message.bookingId)
                .collection(MESSAGES_COLLECTION)
                .document(message.id)
                .set(MessageDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.ChatException.Unknown)
        }
    }

    override suspend fun markMessagesRead(bookingId: String, userId: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(CHATS_COLLECTION)
                .document(bookingId)
                .collection(MESSAGES_COLLECTION)
                .get()
            val unreadFromOtherParty = snapshot.documents
                .map { it.reference to it.data(MessageDto.serializer()) }
                .filter { (_, message) -> !message.isRead && message.senderId != userId }
            if (unreadFromOtherParty.isNotEmpty()) {
                val batch = firestore.batch()
                unreadFromOtherParty.forEach { (ref, _) ->
                    batch.update(ref, mapOf("isRead" to true))
                }
                batch.commit()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.ChatException.Unknown)
        }
    }

    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
    }
}
