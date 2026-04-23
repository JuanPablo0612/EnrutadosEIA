package com.juanpablo0612.carpool.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
    onNavigateToDriver: (User) -> Unit,
    onNavigateToPassenger: (User) -> Unit,
    onNavigateToRoleSelector: (User) -> Unit
) {
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SplashEvent.NavigateToAuth -> onNavigateToAuth()
            is SplashEvent.NavigateToDriver -> onNavigateToDriver(event.user)
            is SplashEvent.NavigateToPassenger -> onNavigateToPassenger(event.user)
            is SplashEvent.NavigateToRoleSelector -> onNavigateToRoleSelector(event.user)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    CarpoolTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
