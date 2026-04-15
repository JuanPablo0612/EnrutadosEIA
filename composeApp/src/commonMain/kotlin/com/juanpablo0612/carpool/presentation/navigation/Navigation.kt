package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordScreen
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginScreen
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterScreen
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object ForgotPassword : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object CreateRoute : Route
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        composable<Route.Login> {
            val viewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                },
                onForgotPasswordClick = {
                    navController.navigate(Route.ForgotPassword)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Register> {
            val viewModel: RegisterViewModel = koinViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.ForgotPassword> {
            val viewModel: ForgotPasswordViewModel = koinViewModel()
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onCreateRouteClick = { navController.navigate(Route.CreateRoute) }
            )
        }

        composable<Route.CreateRoute> {
            val viewModel: CreateRouteViewModel = koinViewModel()
            CreateRouteScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onRouteCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}
