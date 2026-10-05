package com.juanpablo0612.carpool.domain.auth.repository

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: PhoneNumber? = null,
        photoBytes: ByteArray? = null
    ): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Refreshes the signed-in user's auth token and reports whether their email is now verified.
     * Touches Firebase Auth only — no Firestore read — so it is cheap to call whenever the user
     * comes back to the verification screen.
     */
    suspend fun refreshEmailVerification(): Result<Boolean>
    suspend fun logout(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUserId(): String?

    /** The signed-in user's email as Firebase Auth knows it; no Firestore read. */
    fun getCurrentUserEmail(): String?

    suspend fun getCurrentUser(): Result<User>
    suspend fun getPublicProfile(userId: String): Result<PublicProfile>
    suspend fun updateProfile(name: String, phone: PhoneNumber?, bio: String?, photoBytes: ByteArray?): Result<User>
    suspend fun deleteAccount(): Result<Unit>
}
