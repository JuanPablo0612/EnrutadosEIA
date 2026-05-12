package com.juanpablo0612.carpool.presentation.auth.email_verification

import com.juanpablo0612.carpool.domain.auth.model.AuthError

data class EmailVerificationUiState(
    val obfuscatedEmail: String = "",
    val resendCountdown: Int = 0,
    val isLoading: Boolean = false,
    val error: AuthError? = null
)
