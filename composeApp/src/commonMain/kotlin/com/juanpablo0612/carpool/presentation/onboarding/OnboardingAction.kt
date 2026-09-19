package com.juanpablo0612.carpool.presentation.onboarding

sealed class OnboardingAction {
    data object OnNextPage : OnboardingAction()
    data class OnPageChanged(val page: Int) : OnboardingAction()
    data object OnSkip : OnboardingAction()
    data object OnFinish : OnboardingAction()
}
