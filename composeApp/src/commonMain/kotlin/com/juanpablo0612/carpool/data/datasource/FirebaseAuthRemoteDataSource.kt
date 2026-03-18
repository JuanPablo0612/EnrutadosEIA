package com.juanpablo0612.carpool.data.datasource

import com.juanpablo0612.carpool.data.model.UserDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) : AuthRemoteDataSource {

    override suspend fun signIn(email: String, password: String): UserDto {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        return result.user?.toDto() ?: throw Exception("User not found after sign in")
    }

    override suspend fun signUp(email: String, password: String): UserDto {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        return result.user?.toDto() ?: throw Exception("User not found after sign up")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getAuthState(): Flow<UserDto?> {
        return firebaseAuth.authStateChanged.map { it?.toDto() }
    }

    private fun FirebaseUser.toDto(): UserDto {
        return UserDto(
            id = uid,
            email = email ?: "",
            name = displayName,
            isEmailVerified = isEmailVerified
        )
    }
}
