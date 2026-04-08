package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_welcome
import enrutadoseia.composeapp.generated.resources.add_24px
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
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
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate(Route.CreateRoute) }
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add_24px),
                            contentDescription = "Create Route"
                        )
                    }
                }
            ) { padding ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(Res.string.home_welcome))
                }
            }
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
