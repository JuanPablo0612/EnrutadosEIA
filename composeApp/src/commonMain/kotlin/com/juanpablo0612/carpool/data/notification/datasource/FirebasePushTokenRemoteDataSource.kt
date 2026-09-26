package com.juanpablo0612.carpool.data.notification.datasource

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.messaging.FirebaseMessaging
import kotlinx.serialization.Serializable
import kotlin.time.Clock

class FirebasePushTokenRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
) : PushTokenRemoteDataSource {

    override suspend fun register(userId: String) {
        val token = messaging.getToken()
        tokenDocument(userId, token).set(
            PushTokenDto.serializer(),
            PushTokenDto(token = token, updatedAt = Clock.System.now().toEpochMilliseconds()),
        )
    }

    override suspend fun unregister(userId: String) {
        val token = messaging.getToken()
        tokenDocument(userId, token).delete()
        messaging.deleteToken()
    }

    override suspend fun deleteLocalToken() {
        messaging.deleteToken()
    }

    private fun tokenDocument(userId: String, token: String) = firestore
        .collection(USERS_COLLECTION)
        .document(userId)
        .collection(TOKENS_COLLECTION)
        .document(token)

    private companion object {
        const val USERS_COLLECTION = "users"
        const val TOKENS_COLLECTION = "fcmTokens"
    }
}

@Serializable
private data class PushTokenDto(
    val token: String = "",
    val updatedAt: Long = 0L,
)
