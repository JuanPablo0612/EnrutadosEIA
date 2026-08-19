package com.juanpablo0612.carpool.presentation.auth

import com.juanpablo0612.carpool.domain.auth.model.User

sealed class AuthEvent {
    data class NavigateAfterAuth(val user: User) : AuthEvent()
    data object NavigateToEmailVerification : AuthEvent()
}
