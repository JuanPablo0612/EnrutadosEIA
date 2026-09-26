package com.juanpablo0612.carpool.data.notification.datasource

/** Registers this device's push token under the signed-in user so the backend can reach it. */
interface PushTokenRemoteDataSource {
    suspend fun register(userId: String)

    /** Removes this device's token from [userId] and invalidates it. */
    suspend fun unregister(userId: String)

    /** Invalidates this device's token without touching Firestore (the account is being deleted). */
    suspend fun deleteLocalToken()
}
