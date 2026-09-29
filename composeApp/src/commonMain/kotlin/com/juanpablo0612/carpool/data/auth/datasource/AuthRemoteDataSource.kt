package com.juanpablo0612.carpool.data.auth.datasource

import com.juanpablo0612.carpool.data.auth.model.UserDocument

interface AuthRemoteDataSource {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String = "",
        photoBytes: ByteArray? = null
    )
    suspend fun sendEmailVerification()
    suspend fun reloadEmailVerified(): Boolean
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String)
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    suspend fun getCurrentUser(): UserDocument
    suspend fun getPublicProfile(userId: String): UserDocument
    suspend fun updateProfile(name: String, phone: String?, bio: String?, photoBytes: ByteArray?): UserDocument
    suspend fun deleteAccount()
}
