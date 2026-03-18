package com.juanpablo0612.carpool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juanpablo0612.carpool.presentation.state.AuthEvent
import com.juanpablo0612.carpool.presentation.ui.screens.LoginScreen
import com.juanpablo0612.carpool.presentation.ui.screens.RegisterScreen
import com.juanpablo0612.carpool.presentation.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val viewModel: AuthViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.events) {
            viewModel.events.collect { event ->
                when (event) {
                    is AuthEvent.NavigateToHome -> {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            popUpTo("register") { inclusive = true }
                        }
                    }
                    is AuthEvent.NavigateToLogin -> {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                    is AuthEvent.ShowErrorMessage -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            NavHost(
                navController = navController,
                startDestination = if (state.isLoggedIn) "home" else "login"
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {}
                    )
                }
                composable("register") {
                    RegisterScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { navController.popBackStack() },
                        onRegisterSuccess = {}
                    )
                }
                composable("home") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Welcome, ${state.user?.email ?: "User"}!")
                    }
                }
            }
        }
    }
}
