package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordScreen
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginScreen
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterScreen
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleScreen
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleViewModel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.home_24px
import enrutadoseia.composeapp.generated.resources.nav_create_route
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.nav_register_vehicle
import enrutadoseia.composeapp.generated.resources.person_24px
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
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

    @Serializable
    data object RegisterVehicle : Route
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackstackEntry?.destination

    Scaffold(
        bottomBar = {
            val bottomNavItems = listOf(
                BottomNavItem.Home,
                BottomNavItem.CreateRoute,
                BottomNavItem.RegisterVehicle
            )
            val showBottomBar =
                bottomNavItems.any { currentDestination?.hasRoute(it.route::class) == true }

            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Login,
            modifier = modifier.padding(innerPadding)
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

            composable<Route.RegisterVehicle> {
                val viewModel: RegisterVehicleViewModel = koinViewModel()
                RegisterVehicleScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onVehicleRegistered = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem<out Any>>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = vectorResource(item.icon),
                        contentDescription = stringResource(item.label)
                    )
                },
                label = { Text(text = stringResource(item.label)) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().displayName) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem<T : Any>(
    val label: StringResource,
    val icon: DrawableResource,
    val route: T
) {
    data object Home : BottomNavItem<Route.Home>(
        label = Res.string.nav_home,
        icon = Res.drawable.home_24px,
        route = Route.Home
    )

    data object CreateRoute : BottomNavItem<Route.CreateRoute>(
        label = Res.string.nav_create_route,
        icon = Res.drawable.add_24px,
        route = Route.CreateRoute
    )

    data object RegisterVehicle : BottomNavItem<Route.RegisterVehicle>(
        label = Res.string.nav_register_vehicle,
        icon = Res.drawable.person_24px,
        route = Route.RegisterVehicle
    )
}
