package com.juanpablo0612.carpool.presentation.auth.emailverification

import com.juanpablo0612.carpool.presentation.auth.AuthError

data class EmailVerificationUiState(
    val obfuscatedEmail: String = "",
    val resendCountdown: Int = 0,
    val isLoading: Boolean = false,
    /** A verification check is in flight; the check button shows progress meanwhile. */
    val isChecking: Boolean = false,
    /** The user asked to continue but the email is still unverified. */
    val isStillUnverified: Boolean = false,
    val error: AuthError? = null
)
