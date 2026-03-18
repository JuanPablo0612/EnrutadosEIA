package com.juanpablo0612.carpool.data.auth.mapper

import com.juanpablo0612.carpool.data.auth.model.UserDto
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User
import dev.gitlive.firebase.auth.FirebaseAuthException

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        isEmailVerified = isEmailVerified
    )
}

fun mapFirebaseError(e: FirebaseAuthException): AuthError {
    val message = e.message ?: ""
    return when {
        message.contains("wrong-password", true) || message.contains("user-not-found", true) -> AuthError.InvalidCredentials
        message.contains("email-already-in-use", true) -> AuthError.EmailAlreadyInUse
        message.contains("network-request-failed", true) -> AuthError.NetworkError
        message.contains("weak-password", true) -> AuthError.WeakPassword
        else -> AuthError.UnknownError
    }
}
