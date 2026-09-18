package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.driverNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.passengerNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.rootNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.sharedNavGraph
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val MAP_PICK_RESULT_KEY = "map_pick_result"

/**
 * Which role's nav graph a route belongs to, or `null` for role-agnostic/shared routes. Used so a
 * deep link can switch [UserSession.activeRole] to match its target before navigating, instead of
 * leaving a dual-role user's bottom bar/theme desynced from the screen they land on.
 */
private fun Route.requiredRoleOrNull(): UserRole? = when (this) {
    is Route.Home, is Route.RoutesList, is Route.CreateRoute, is Route.RouteDetail,
    is Route.CreateTrip, is Route.DriverTrips, is Route.TripPassengers, is Route.VehiclesList,
    is Route.RegisterVehicle, is Route.DriverBookingRequests, is Route.PassengerProfile -> UserRole.Driver

    is Route.PassengerHome, is Route.TripDetailPassenger, is Route.PassengerBookings -> UserRole.Passenger

    else -> null
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val userSession = koinInject<UserSession>()
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()
    val activeRole by userSession.activeRole.collectAsState()

    val navBackstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackstackEntry?.destination

    val driverBottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyTrips,
        BottomNavItem.BookingRequests,
        BottomNavItem.Profile
    )
    val passengerBottomNavItems = listOf(
        BottomNavItem.SearchRoutes,
        BottomNavItem.PassengerBookings,
        BottomNavItem.Profile
    )
    val showDriverBottomBar = driverBottomNavItems.any {
        currentDestination?.hasRoute(it.route::class) == true
    }
    val showPassengerBottomBar = passengerBottomNavItems.any {
        currentDestination?.hasRoute(it.route::class) == true
    }
    val showBottomBar = showDriverBottomBar || showPassengerBottomBar
    val currentBottomNavItems = when {
        showDriverBottomBar && showPassengerBottomBar -> {
            if (activeRole == UserRole.Passenger) passengerBottomNavItems else driverBottomNavItems
        }
        showDriverBottomBar -> driverBottomNavItems
        showPassengerBottomBar -> passengerBottomNavItems
        else -> emptyList()
    }

    // The one way to change active role. Every caller resets the back stack, because leaving the
    // previous role's destinations underneath is exactly how activeRole ends up disagreeing with
    // the tab set that is actually on screen — the desync the bottom-bar comment below guards
    // against, reached from the other direction. `destination` defaults to that role's home
    // screen, but a caller landing somewhere more specific (e.g. a deep link target) can override
    // it while still getting the same role-set-then-reset-stack behavior.
    fun switchActiveRole(
        role: UserRole,
        destination: Route = if (role == UserRole.Driver) Route.Home else Route.PassengerHome
    ) {
        userSession.setActiveRole(role)
        navController.navigate(destination) {
            popUpTo(0) { inclusive = true }
        }
    }

    val onLogout: () -> Unit = {
        scope.launch {
            authRepository.logout()
            userSession.clearSession()
            navController.navigate(Route.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CarpoolTheme(role = activeRole) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        items = currentBottomNavItems,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // Anchor on the same signal that picked currentBottomNavItems
                                // (destination-driven), not on activeRole directly — activeRole
                                // is a separately-updated field that can momentarily disagree
                                // with which tab set is actually on screen, which would pop the
                                // back stack to the wrong role's root.
                                if (currentBottomNavItems === passengerBottomNavItems) {
                                    popUpTo<Route.PassengerHome> { saveState = true }
                                } else {
                                    popUpTo<Route.Home> { saveState = true }
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
                // Forward and back navigation were visually identical (NavHost's default fade),
                // so the app gave no directional cue about depth. Declared once here rather than
                // per-destination.
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) + fadeIn()
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) + fadeOut()
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) + fadeIn()
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) + fadeOut()
                },
                // consumeWindowInsets, not just padding: Modifier.padding does not mark the
                // insets as consumed, so each screen's own Scaffold would apply the navigation
                // bar inset a second time on top of the space the bottom bar already took.
                modifier = modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                rootNavGraph(
                    onSplashNavigateToAuth = {
                        navController.navigate(Route.Login) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onSplashNavigateToOnboarding = {
                        navController.navigate(Route.Onboarding) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onSplashNavigateToEmailVerification = {
                        navController.navigate(Route.EmailVerification) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onSplashNavigateToDriver = { user ->
                        userSession.setSession(user, UserRole.Driver)
                        navController.navigate(Route.Home) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onSplashNavigateToPassenger = { user ->
                        userSession.setSession(user, UserRole.Passenger)
                        navController.navigate(Route.PassengerHome) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onSplashNavigateToRoleSelector = { user ->
                        userSession.setUser(user)
                        navController.navigate(Route.RoleSelector) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    },
                    onOnboardingNavigateToApp = {
                        navController.navigate(Route.Splash) {
                            popUpTo<Route.Onboarding> { inclusive = true }
                        }
                    },
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
                    onNavigateToEmailVerification = { navController.navigate(Route.EmailVerification) },
                    onNavigateBack = { navController.popBackStack() }
                )

                driverNavGraph(
                    // A direct toggle: RoleSelector stays for first run and the explicit
                    // "remember my choice" flow, not for flipping a binary you already know.
                    onSwitchRole = { switchActiveRole(UserRole.Passenger) },
                    onNavigateToProfile = { navController.navigate(Route.Profile) },
                    onNavigateToCreateRoute = { navController.navigate(Route.CreateRoute) },
                    onNavigateToRegisterVehicle = { navController.navigate(Route.RegisterVehicle()) },
                    onNavigateToEditVehicle = { id -> navController.navigate(Route.RegisterVehicle(id)) },
                    onNavigateToRouteDetail = { routeId -> navController.navigate(Route.RouteDetail(routeId)) },
                    onNavigateToCreateTrip = { routeId -> navController.navigate(Route.CreateTrip(routeId)) },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateToRoutesList = { navController.navigate(Route.RoutesList) },
                    onNavigateToDriverTrips = { navController.navigate(Route.DriverTrips) },
                    onNavigateToDriverBookingRequests = { navController.navigate(Route.DriverBookingRequests) },
                    onNavigateToSearchTrips = { switchActiveRole(UserRole.Passenger) },
                    onNavigateToPassengerBookings = { navController.navigate(Route.PassengerBookings) },
                    onNavigateToSavedPlaces = { navController.navigate(Route.SavedPlaces) },
                    onNavigateToVehiclesList = { navController.navigate(Route.VehiclesList) },
                    onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onNavigateToTripDetailPassenger = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                    onNavigateToPassengers = { tripId -> navController.navigate(Route.TripPassengers(tripId)) },
                    onNavigateToPassengerProfile = { userId -> navController.navigate(Route.PassengerProfile(userId)) },
                    onNavigateToRating = { bookingId, tripId, rateeId, rateeName ->
                        // Driver side, so the ratee is always the passenger — selects the
                        // passenger chip set, mirroring the passenger graph's rateeIsDriver = true.
                        navController.navigate(
                            Route.PostTripRating(bookingId, tripId, rateeId, rateeName, rateeIsDriver = false)
                        )
                    },
                    onNavigateToChat = { bookingId, tripId, otherPartyName, isReadOnly ->
                        navController.navigate(Route.Chat(bookingId, tripId, otherPartyName, isReadOnly))
                    },
                    onNavigateBack = { navController.popBackStack() },
                )

                passengerNavGraph(
                    onSwitchRole = { switchActiveRole(UserRole.Driver) },
                    onNavigateToProfile = { navController.navigate(Route.Profile) },
                    onNavigateToTripDetail = { tripId ->
                        navController.navigate(Route.TripDetailPassenger(tripId))
                    },
                    onNavigateToPassengerBookings = {
                        navController.navigate(Route.PassengerBookings)
                    },
                    onNavigateToTripTracking = { tripId ->
                        navController.navigate(Route.TripTracking(tripId))
                    },
                    onNavigateToRating = { bookingId, tripId, rateeId, rateeName ->
                        // This graph is the passenger side, so the ratee is always the driver —
                        // which is what selects the "clean car / safe driving" chip set.
                        navController.navigate(
                            Route.PostTripRating(bookingId, tripId, rateeId, rateeName, rateeIsDriver = true)
                        )
                    },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateBack = { navController.popBackStack() }
                )

                sharedNavGraph(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMapPicker = { lat, lon ->
                        navController.navigate(Route.MapPicker(lat ?: 6.1633, lon ?: -75.4913))
                    },
                    onCoordinatesPicked = { lat, lon ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(MAP_PICK_RESULT_KEY, "$lat,$lon")
                        navController.popBackStack()
                    },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateToRoutes = { navController.navigate(Route.RoutesList) },
                    onNavigateToVehicles = { navController.navigate(Route.VehiclesList) },
                    onLogout = onLogout,
                    onNavigateToEditProfile = { navController.navigate(Route.EditProfile) },
                    // The list, not the creation form — the row is labelled "saved places".
                    onNavigateToSavedPlaces = { navController.navigate(Route.SavedPlaces) },
                    onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                    onNavigateToSafety = { navController.navigate(Route.Safety) },
                    onDeleteAccountSuccess = {
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onRoleSwitched = { role ->
                        // ProfileViewModel already flipped userSession.activeRole — just move
                        // the nav graph so it agrees (3.9).
                        val destination =
                            if (role == UserRole.Driver) Route.Home else Route.PassengerHome
                        navController.navigate(destination) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToDeepLink = { deepLink ->
                        deepLink.toRouteOrNull()?.let { route ->
                            val requiredRole = route.requiredRoleOrNull()
                            if (requiredRole != null && requiredRole != activeRole) {
                                // Land on the deep link's actual target, not that role's home
                                // screen, while still keeping activeRole/bottom-bar/theme in
                                // sync with where the user is actually being sent.
                                switchActiveRole(requiredRole, route)
                            } else {
                                navController.navigate(route)
                            }
                        }
                    },
                    onNavigateToChat = { bookingId, tripId, otherPartyName, isReadOnly ->
                        navController.navigate(Route.Chat(bookingId, tripId, otherPartyName, isReadOnly))
                    }
                )
            }
        }
    }
}
