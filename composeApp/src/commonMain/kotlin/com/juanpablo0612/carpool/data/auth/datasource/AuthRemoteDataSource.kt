package com.juanpablo0612.carpool.data.auth.datasource

import com.juanpablo0612.carpool.data.auth.model.UserDto

interface AuthRemoteDataSource {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean,
        phone: String = "",
        photoBytes: ByteArray? = null
    )
    suspend fun sendEmailVerification()
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String)
    fun getCurrentUserId(): String?
    suspend fun getCurrentUser(): UserDto
    suspend fun getPublicProfile(userId: String): UserDto
    suspend fun updateProfile(name: String, phone: String?, bio: String?, photoUrl: String?): UserDto
    suspend fun updateRoles(isDriver: Boolean, isPassenger: Boolean): UserDto
    suspend fun deleteAccount()
}
