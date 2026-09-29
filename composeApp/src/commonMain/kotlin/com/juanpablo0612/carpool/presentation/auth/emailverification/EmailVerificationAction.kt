package com.juanpablo0612.carpool.presentation.auth.emailverification

sealed class EmailVerificationAction {
    data object OnResendEmail : EmailVerificationAction()
    data object OnCountdownTick : EmailVerificationAction()

    /** The user says they opened the link ("Ya lo verifiqué"). */
    data object OnCheckVerification : EmailVerificationAction()

    /** The screen came to the foreground, e.g. back from the mail app or the browser. */
    data object OnScreenResumed : EmailVerificationAction()
}
