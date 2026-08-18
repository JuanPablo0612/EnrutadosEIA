package com.juanpablo0612.carpool.data.chat.model

import com.juanpablo0612.carpool.domain.chat.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String = "",
    val bookingId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
) {
    fun toDomain(): Message = Message(
        id = id,
        bookingId = bookingId,
        senderId = senderId,
        text = text,
        timestamp = timestamp,
        isRead = isRead
    )

    companion object {
        fun fromDomain(message: Message): MessageDto = MessageDto(
            id = message.id,
            bookingId = message.bookingId,
            senderId = message.senderId,
            text = message.text,
            timestamp = message.timestamp,
            isRead = message.isRead
        )
    }
}
