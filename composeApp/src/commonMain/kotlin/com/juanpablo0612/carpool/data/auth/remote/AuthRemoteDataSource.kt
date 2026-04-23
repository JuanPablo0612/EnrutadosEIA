package com.juanpablo0612.carpool.data.auth.remote

import com.juanpablo0612.carpool.data.auth.model.UserDto

interface AuthRemoteDataSource {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean
    )
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String)
    fun getCurrentUserId(): String?
    suspend fun getCurrentUser(): UserDto
}
