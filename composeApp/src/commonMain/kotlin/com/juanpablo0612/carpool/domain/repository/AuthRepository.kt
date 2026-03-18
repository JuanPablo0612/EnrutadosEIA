package com.juanpablo0612.carpool.domain.repository

import com.juanpablo0612.carpool.core.AppResult
import com.juanpablo0612.carpool.domain.error.AuthError
import com.juanpablo0612.carpool.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User, AuthError>
    suspend fun register(email: String, password: String): AppResult<User, AuthError>
    suspend fun logout(): AppResult<Unit, AuthError>
    fun observeAuthState(): Flow<User?>
}
