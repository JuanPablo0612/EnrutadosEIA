package com.juanpablo0612.carpool.data.auth.remote

import com.juanpablo0612.carpool.data.auth.model.UserDto
import kotlinx.coroutines.flow.Flow

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
}
