package com.juanpablo0612.carpool.presentation.splash

import com.juanpablo0612.carpool.domain.auth.model.User

sealed class SplashEvent {
    data object NavigateToAuth : SplashEvent()
    data class NavigateToDriver(val user: User) : SplashEvent()
    data class NavigateToPassenger(val user: User) : SplashEvent()
    data class NavigateToRoleSelector(val user: User) : SplashEvent()
}
