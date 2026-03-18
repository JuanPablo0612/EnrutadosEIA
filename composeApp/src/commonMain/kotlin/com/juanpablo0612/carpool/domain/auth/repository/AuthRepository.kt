package com.juanpablo0612.carpool.domain.auth.repository

import com.juanpablo0612.carpool.core.result.AppResult
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User, AuthError>
    suspend fun register(email: String, password: String): AppResult<User, AuthError>
    suspend fun logout(): AppResult<Unit, AuthError>
    fun observeAuthState(): Flow<User?>
}
