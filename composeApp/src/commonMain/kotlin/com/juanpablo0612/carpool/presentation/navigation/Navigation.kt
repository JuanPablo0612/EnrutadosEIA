package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.mainNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.passengerNavGraph
import com.juanpablo0612.carpool.presentation.role_selector.RoleSelectorScreen
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.splash.SplashScreen
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val userSession = koinInject<UserSession>()
    val logoutUseCase = koinInject<LogoutUseCase>()
    val scope = rememberCoroutineScope()

    val navBackstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackstackEntry?.destination

    val driverBottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyRoutes,
        BottomNavItem.MyVehicles,
        BottomNavItem.MyTrips,
        BottomNavItem.BookingRequests
    )
    val passengerBottomNavItems = listOf(
        BottomNavItem.SearchRoutes,
        BottomNavItem.PassengerBookings
    )
    val showDriverBottomBar = driverBottomNavItems.any {
        currentDestination?.hasRoute(it.route::class) == true
    }
    val showPassengerBottomBar = passengerBottomNavItems.any {
        currentDestination?.hasRoute(it.route::class) == true
    }
    val showBottomBar = showDriverBottomBar || showPassengerBottomBar
    val currentBottomNavItems = when {
        showDriverBottomBar -> driverBottomNavItems
        showPassengerBottomBar -> passengerBottomNavItems
        else -> emptyList()
    }

    val onLogout: () -> Unit = {
        scope.launch {
            logoutUseCase()
            userSession.clearSession()
            navController.navigate(Route.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    items = currentBottomNavItems,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            if (showDriverBottomBar) {
                                popUpTo<Route.Home> { saveState = true }
                            } else {
                                popUpTo<Route.PassengerHome> { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            modifier = modifier.padding(innerPadding)
        ) {
            composable<Route.Splash> {
                val viewModel: SplashViewModel = koinViewModel()
                SplashScreen(
                    viewModel = viewModel,
                    onNavigateToAuth = {
                        navController.navigate(Route.Login) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onNavigateToDriver = { user ->
                        userSession.setSession(user, UserRole.Driver)
                        navController.navigate(Route.Home) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onNavigateToPassenger = { user ->
                        userSession.setSession(user, UserRole.Passenger)
                        navController.navigate(Route.PassengerHome) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onNavigateToRoleSelector = { user ->
                        userSession.setUser(user)
                        navController.navigate(Route.RoleSelector) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.RoleSelector> {
                val user by userSession.user.collectAsState()
                user?.let { u ->
                    RoleSelectorScreen(
                        user = u,
                        onSelectDriver = {
                            userSession.setActiveRole(UserRole.Driver)
                            navController.navigate(Route.Home) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onSelectPassenger = {
                            userSession.setActiveRole(UserRole.Passenger)
                            navController.navigate(Route.PassengerHome) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            authNavGraph(
                onAuthSuccess = { user ->
                    when {
                        user.isDriver && user.isPassenger -> {
                            userSession.setUser(user)
                            navController.navigate(Route.RoleSelector) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        user.isDriver -> {
                            userSession.setSession(user, UserRole.Driver)
                            navController.navigate(Route.Home) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        user.isPassenger -> {
                            userSession.setSession(user, UserRole.Passenger)
                            navController.navigate(Route.PassengerHome) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Route.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(Route.Register) },
                onNavigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                onNavigateBack = { navController.popBackStack() }
            )

            mainNavGraph(
                onSwitchRole = {
                    navController.navigate(Route.RoleSelector) {
                        popUpTo(0) { inclusive = false }
                    }
                },
                onLogout = onLogout,
                onNavigateToCreateRoute = { navController.navigate(Route.CreateRoute) },
                onNavigateToRegisterVehicle = { navController.navigate(Route.RegisterVehicle) },
                onNavigateToRouteDetail = { routeId -> navController.navigate(Route.RouteDetail(routeId)) },
                onNavigateToCreateTrip = { routeId -> navController.navigate(Route.CreateTrip(routeId)) },
                onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                onNavigateBack = { navController.popBackStack() }
            )

            passengerNavGraph(
                onSwitchRole = {
                    navController.navigate(Route.RoleSelector) {
                        popUpTo(0) { inclusive = false }
                    }
                },
                onLogout = onLogout,
                onNavigateToTripDetail = { tripId ->
                    navController.navigate(Route.TripDetailPassenger(tripId))
                },
                onNavigateToPassengerBookings = {
                    navController.navigate(Route.PassengerBookings)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
