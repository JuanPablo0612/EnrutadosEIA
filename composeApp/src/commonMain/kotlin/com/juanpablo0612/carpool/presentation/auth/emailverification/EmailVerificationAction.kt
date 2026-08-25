package com.juanpablo0612.carpool.presentation.auth.emailverification

sealed class EmailVerificationAction {
    data object OnResendEmail : EmailVerificationAction()
    data object OnCountdownTick : EmailVerificationAction()
    data object OnOpenGmail : EmailVerificationAction()
}
