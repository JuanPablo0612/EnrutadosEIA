package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordScreen
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginScreen
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterScreen
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.authNavGraph(
    onAuthSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<Route.Login> {
        val viewModel: LoginViewModel = koinViewModel()
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = onAuthSuccess,
            onNavigateToRegister = onNavigateToRegister,
            onForgotPasswordClick = onNavigateToForgotPassword,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.Register> {
        val viewModel: RegisterViewModel = koinViewModel()
        RegisterScreen(
            viewModel = viewModel,
            onRegisterSuccess = onAuthSuccess,
            onNavigateToLogin = onNavigateBack,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.ForgotPassword> {
        val viewModel: ForgotPasswordViewModel = koinViewModel()
        ForgotPasswordScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack
        )
    }
}
