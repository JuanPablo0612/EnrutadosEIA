package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingScreen
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingViewModel
import com.juanpablo0612.carpool.presentation.splash.SplashScreen
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.rootNavGraph(
    onSplashNavigateToAuth: () -> Unit,
    onSplashNavigateToOnboarding: () -> Unit,
    onSplashNavigateToEmailVerification: () -> Unit,
    onSplashNavigateToHome: (User) -> Unit,
    onOnboardingNavigateToApp: () -> Unit,
) {
    composable<Route.Splash> {
        val viewModel: SplashViewModel = koinViewModel()
        SplashScreen(
            viewModel = viewModel,
            onNavigateToAuth = onSplashNavigateToAuth,
            onNavigateToOnboarding = onSplashNavigateToOnboarding,
            onNavigateToEmailVerification = onSplashNavigateToEmailVerification,
            onNavigateToHome = onSplashNavigateToHome,
        )
    }

    composable<Route.Onboarding> {
        val viewModel: OnboardingViewModel = koinViewModel()
        OnboardingScreen(
            viewModel = viewModel,
            onNavigateToApp = onOnboardingNavigateToApp
        )
    }
}
