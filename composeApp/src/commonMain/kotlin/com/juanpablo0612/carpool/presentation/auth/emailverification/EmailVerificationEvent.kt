package com.juanpablo0612.carpool.presentation.auth.emailverification

import com.juanpablo0612.carpool.domain.auth.model.User

sealed class EmailVerificationEvent {
    data class NavigateToApp(val user: User) : EmailVerificationEvent()
    data object OpenGmail : EmailVerificationEvent()
}
