package com.juanpablo0612.carpool.domain.chat.model

data class Message(
    val id: String,
    val bookingId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean
)
