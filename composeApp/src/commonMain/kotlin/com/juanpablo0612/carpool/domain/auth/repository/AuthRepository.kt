package com.juanpablo0612.carpool.domain.auth.repository

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
}
