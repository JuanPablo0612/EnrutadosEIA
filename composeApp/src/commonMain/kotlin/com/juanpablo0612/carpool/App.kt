package com.juanpablo0612.carpool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.login.LoginScreen
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterScreen
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    CarpoolTheme {
        val navController = rememberNavController()
        val loginViewModel: LoginViewModel = koinViewModel()
        val registerViewModel: RegisterViewModel = koinViewModel()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            loginViewModel.events.collect { event ->
                handleAuthEvent(event, navController, snackbarHostState)
            }
        }

        LaunchedEffect(Unit) {
            registerViewModel.events.collect { event ->
                handleAuthEvent(event, navController, snackbarHostState)
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        viewModel = registerViewModel,
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }
                composable("home") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Welcome to Home!")
                    }
                }
            }
        }
    }
}

private suspend fun handleAuthEvent(
    event: AuthEvent,
    navController: androidx.navigation.NavHostController,
    snackbarHostState: SnackbarHostState
) {
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
