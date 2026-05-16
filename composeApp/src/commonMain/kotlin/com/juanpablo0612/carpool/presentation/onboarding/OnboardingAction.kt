package com.juanpablo0612.carpool.presentation.onboarding

sealed class OnboardingAction {
    data object OnNextPage : OnboardingAction()
    data object OnSkip : OnboardingAction()
    data object OnFinish : OnboardingAction()
}
