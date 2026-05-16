package com.juanpablo0612.carpool.presentation.onboarding

sealed class OnboardingEvent {
    data object NavigateToApp : OnboardingEvent()
}
