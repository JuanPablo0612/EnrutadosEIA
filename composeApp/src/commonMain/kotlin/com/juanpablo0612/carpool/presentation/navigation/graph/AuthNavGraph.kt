package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.auth.emailverification.EmailVerificationScreen
import com.juanpablo0612.carpool.presentation.auth.emailverification.EmailVerificationViewModel
import com.juanpablo0612.carpool.presentation.auth.entry.EntryScreen
import com.juanpablo0612.carpool.presentation.auth.forgotpassword.ForgotPasswordScreen
import com.juanpablo0612.carpool.presentation.auth.forgotpassword.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginScreen
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterScreen
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.authNavGraph(
    onAuthSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    onSwitchToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToEmailVerification: () -> Unit,
    onSignUpAgain: () -> Unit,
    onNavigateBack: () -> Unit,
    canNavigateBack: () -> Boolean = { true }
) {
    composable<Route.Entry> {
        EntryScreen(
            onCreateAccountClick = onNavigateToRegister,
            onSignInClick = onNavigateToLogin,
        )
    }

    composable<Route.Login> {
        val viewModel: LoginViewModel = koinViewModel()
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = onAuthSuccess,
            onNavigateToRegister = onNavigateToRegister,
            onForgotPasswordClick = onNavigateToForgotPassword,
            onNavigateToEmailVerification = onNavigateToEmailVerification,
            onBackClick = onNavigateBack,
            canNavigateBack = canNavigateBack()
        )
    }

    composable<Route.Register> {
        val viewModel: RegisterViewModel = koinViewModel()
        RegisterScreen(
            viewModel = viewModel,
            onRegisterSuccess = onAuthSuccess,
            onNavigateToEmailVerification = onNavigateToEmailVerification,
            onNavigateToLogin = onSwitchToLogin,
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

    composable<Route.EmailVerification> {
        val viewModel: EmailVerificationViewModel = koinViewModel()
        EmailVerificationScreen(
            viewModel = viewModel,
            onNavigateToApp = onAuthSuccess,
            onNavigateToSignUp = onSignUpAgain,
        )
    }
}
