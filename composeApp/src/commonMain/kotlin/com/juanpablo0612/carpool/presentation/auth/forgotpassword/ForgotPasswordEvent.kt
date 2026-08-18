package com.juanpablo0612.carpool.presentation.auth.forgotpassword

sealed class ForgotPasswordEvent {
    data object OpenGmail : ForgotPasswordEvent()
}
