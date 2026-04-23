package com.juanpablo0612.carpool.domain.auth.repository

import com.juanpablo0612.carpool.domain.auth.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean
    ): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUserId(): String?
    suspend fun getCurrentUser(): Result<User>
}
