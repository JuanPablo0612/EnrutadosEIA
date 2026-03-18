package com.juanpablo0612.carpool.presentation.state

import com.juanpablo0612.carpool.domain.error.AuthError
import com.juanpablo0612.carpool.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: AuthError? = null,
    val isLoggedIn: Boolean = false
)
