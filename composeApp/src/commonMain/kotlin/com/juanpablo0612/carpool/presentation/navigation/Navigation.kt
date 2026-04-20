package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.mainNavGraph

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackstackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.CreateRoute,
        BottomNavItem.RegisterVehicle
    )
    val showBottomBar = bottomNavItems.any { currentDestination?.hasRoute(it.route::class) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    items = bottomNavItems,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().displayName) {
                                saveState = true
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
            startDestination = Route.Login,
            modifier = modifier.padding(innerPadding)
        ) {
            authNavGraph(
                onAuthSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Route.Register) },
                onNavigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                onNavigateBack = { navController.popBackStack() }
            )
            mainNavGraph(
                onNavigateToCreateRoute = { navController.navigate(Route.CreateRoute) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
