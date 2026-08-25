package com.juanpablo0612.carpool.data.chat.datasource

import com.juanpablo0612.carpool.data.chat.model.MessageDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseChatRemoteDataSource(
    private val firestore: FirebaseFirestore
) : ChatRemoteDataSource {

    override fun getMessages(bookingId: String): Flow<List<MessageDto>> {
        return firestore.collection(CHATS_COLLECTION)
            .document(bookingId)
            .collection(MESSAGES_COLLECTION)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(MessageDto.serializer()) }
            }
    }

    override suspend fun sendMessage(bookingId: String, messageId: String, message: MessageDto) {
        firestore.collection(CHATS_COLLECTION)
            .document(bookingId)
            .collection(MESSAGES_COLLECTION)
            .document(messageId)
            .set(MessageDto.serializer(), message)
    }

    override suspend fun markMessagesRead(bookingId: String, userId: String) {
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
    }

    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
    }
}
