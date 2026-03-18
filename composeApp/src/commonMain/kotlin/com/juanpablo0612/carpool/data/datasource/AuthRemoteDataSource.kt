package com.juanpablo0612.carpool.data.datasource

import com.juanpablo0612.carpool.data.model.UserDto
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    suspend fun signIn(email: String, password: String): UserDto
    suspend fun signUp(email: String, password: String): UserDto
    suspend fun signOut()
    fun getAuthState(): Flow<UserDto?>
}
