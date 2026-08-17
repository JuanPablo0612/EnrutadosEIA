package com.juanpablo0612.carpool.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDriver: (User) -> Unit,
    onNavigateToPassenger: (User) -> Unit,
    onNavigateToRoleSelector: (User) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SplashEvent.NavigateToAuth -> onNavigateToAuth()
            SplashEvent.NavigateToOnboarding -> onNavigateToOnboarding()
            is SplashEvent.NavigateToDriver -> onNavigateToDriver(event.user)
            is SplashEvent.NavigateToPassenger -> onNavigateToPassenger(event.user)
            is SplashEvent.NavigateToRoleSelector -> onNavigateToRoleSelector(event.user)
        }
    }

    SplashContent(state = state)
}

@Composable
fun SplashContent(state: SplashUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    CarpoolTheme {
        SplashContent(state = SplashUiState())
    }
}
