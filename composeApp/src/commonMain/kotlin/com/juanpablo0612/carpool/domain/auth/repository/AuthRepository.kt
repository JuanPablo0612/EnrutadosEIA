package com.juanpablo0612.carpool.domain.auth.repository

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String = "",
        photoBytes: ByteArray? = null
    ): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUserId(): String?
    suspend fun getCurrentUser(): Result<User>
    suspend fun getPublicProfile(userId: String): Result<PublicProfile>
    suspend fun updateProfile(name: String, phone: String?, bio: String?, photoBytes: ByteArray?): Result<User>
    suspend fun deleteAccount(): Result<Unit>
}
