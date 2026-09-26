package com.juanpablo0612.carpool.presentation.splash

import com.juanpablo0612.carpool.domain.auth.model.User

sealed class SplashEvent {
    data object NavigateToAuth : SplashEvent()
    data object NavigateToOnboarding : SplashEvent()
    data object NavigateToEmailVerification : SplashEvent()
    data class NavigateToHome(val user: User) : SplashEvent()
}
