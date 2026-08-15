package com.juanpablo0612.carpool.presentation.navigation

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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import androidx.compose.runtime.LaunchedEffect
import com.juanpablo0612.carpool.presentation.chat.ChatScreen
import com.juanpablo0612.carpool.presentation.chat.ChatViewModel
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceAction
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceScreen
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.places.picker.MapPickerScreen
import com.juanpablo0612.carpool.presentation.places.picker.MapPickerViewModel
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorMode
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.mainNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.passengerNavGraph
import com.juanpablo0612.carpool.presentation.notifications.NotificationsScreen
import com.juanpablo0612.carpool.presentation.notifications.NotificationsViewModel
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingScreen
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingViewModel
import com.juanpablo0612.carpool.presentation.profile.ProfileScreen
import com.juanpablo0612.carpool.presentation.profile.ProfileViewModel
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileScreen
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileViewModel
import com.juanpablo0612.carpool.presentation.rating.RatingScreen
import com.juanpablo0612.carpool.presentation.rating.RatingViewModel
import com.juanpablo0612.carpool.presentation.role_selector.RoleSelectorScreen
import com.juanpablo0612.carpool.presentation.role_selector.RoleSelectorViewModel
import com.juanpablo0612.carpool.presentation.safety.SafetyScreen
import com.juanpablo0612.carpool.presentation.safety.SafetyViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.splash.SplashScreen
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingScreen
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingViewModel
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val MAP_PICK_RESULT_KEY = "map_pick_result"

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val userSession = koinInject<UserSession>()
    val logoutUseCase = koinInject<LogoutUseCase>()
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

    val onLogout: () -> Unit = {
        scope.launch {
            logoutUseCase()
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
                // consumeWindowInsets, not just padding: Modifier.padding does not mark the
                // insets as consumed, so each screen's own Scaffold would apply the navigation
                // bar inset a second time on top of the space the bottom bar already took.
                modifier = modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
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
                        onNavigateToOnboarding = {
                            navController.navigate(Route.Onboarding) {
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

                composable<Route.Onboarding> {
                    val viewModel: OnboardingViewModel = koinViewModel()
                    OnboardingScreen(
                        viewModel = viewModel,
                        onNavigateToApp = {
                            navController.navigate(Route.Splash) {
                                popUpTo<Route.Onboarding> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Route.RoleSelector> {
                    val viewModel: RoleSelectorViewModel = koinViewModel()
                    RoleSelectorScreen(
                        viewModel = viewModel,
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

                mainNavGraph(
                    onSwitchRole = {
                        navController.navigate(Route.RoleSelector) {
                            popUpTo(0) { inclusive = false }
                        }
                    },
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
                    onNavigateToSearchTrips = {
                        userSession.setActiveRole(UserRole.Passenger)
                        navController.navigate(Route.PassengerHome)
                    },
                    onNavigateToPassengerBookings = { navController.navigate(Route.PassengerBookings) },
                    onNavigateToSavedPlaces = { navController.navigate(Route.SavedPlaces) },
                    onNavigateToVehiclesList = { navController.navigate(Route.VehiclesList) },
                    onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onNavigateToTripDetailPassenger = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                    onNavigateBack = { navController.popBackStack() },
                )

                passengerNavGraph(
                    onSwitchRole = {
                        navController.navigate(Route.RoleSelector) {
                            popUpTo(0) { inclusive = false }
                        }
                    },
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
                    onNavigateBack = { navController.popBackStack() }
                )

                composable<Route.AddPlace> { backStackEntry ->
                    val viewModel: AddPlaceViewModel = koinViewModel()
                    val mapPickResult by backStackEntry.savedStateHandle
                        .getStateFlow<String?>(MAP_PICK_RESULT_KEY, null)
                        .collectAsState()

                    LaunchedEffect(mapPickResult) {
                        mapPickResult?.let { raw ->
                            val parts = raw.split(",")
                            viewModel.onAction(
                                AddPlaceAction.OnMapPickResult(parts[0].toDouble(), parts[1].toDouble())
                            )
                            backStackEntry.savedStateHandle.remove<String>(MAP_PICK_RESULT_KEY)
                        }
                    }

                    AddPlaceScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onPlaceSaved = { navController.popBackStack() },
                        onNavigateToMapPicker = { lat, lon ->
                            navController.navigate(Route.MapPicker(lat ?: 6.1633, lon ?: -75.4913))
                        }
                    )
                }

                composable<Route.SavedPlaces> {
                    val viewModel: PlaceSelectorViewModel = koinViewModel {
                        parametersOf(PlaceSelectorMode.MY_PLACES_KEY)
                    }
                    val state by viewModel.state.collectAsState()
                    // Reuses the browse-and-delete surface that already existed but was never
                    // registered as a destination; only the selection callback is dropped, since
                    // here the list is the destination rather than a picker.
                    PlaceSelectorContent(
                        state = state,
                        onAction = viewModel::onAction,
                        onBack = { navController.popBackStack() },
                        onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    )
                }

                composable<Route.MapPicker> { backStackEntry ->
                    val args = backStackEntry.toRoute<Route.MapPicker>()
                    val viewModel: MapPickerViewModel = koinViewModel {
                        parametersOf(args.initialLatitude, args.initialLongitude)
                    }
                    MapPickerScreen(
                        viewModel = viewModel,
                        onCoordinatesPicked = { lat, lon ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(MAP_PICK_RESULT_KEY, "$lat,$lon")
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                    )
                }

                composable<Route.Profile> {
                    val viewModel: ProfileViewModel = koinViewModel()
                    ProfileScreen(
                        viewModel = viewModel,
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
                        }
                    )
                }

                composable<Route.EditProfile> {
                    val viewModel: EditProfileViewModel = koinViewModel()
                    EditProfileScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }

                composable<Route.Notifications> {
                    val viewModel: NotificationsViewModel = koinViewModel()
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        // The ViewModel already parses a deep link off each notification; without
                        // this the parsed destination was dropped and tapping only marked it read.
                        onNavigateTo = { deepLink ->
                            deepLink.toRouteOrNull()?.let { navController.navigate(it) }
                        }
                    )
                }

                composable<Route.Safety> {
                    val viewModel: SafetyViewModel = koinViewModel()
                    SafetyScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Route.Chat> { backStackEntry ->
                    val args = backStackEntry.toRoute<Route.Chat>()
                    val viewModel: ChatViewModel = koinViewModel {
                        parametersOf(args.bookingId, "", false)
                    }
                    ChatScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<Route.TripTracking> { backStackEntry ->
                    val args = backStackEntry.toRoute<Route.TripTracking>()
                    val viewModel: TripTrackingViewModel = koinViewModel { parametersOf(args.tripId) }
                    TripTrackingScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToChat = { bookingId ->
                            navController.navigate(Route.Chat(bookingId))
                        },
                        onTripCompleted = { navController.popBackStack() }
                    )
                }

                composable<Route.PostTripRating> { backStackEntry ->
                    val args = backStackEntry.toRoute<Route.PostTripRating>()
                    val viewModel: RatingViewModel = koinViewModel {
                        parametersOf(args.bookingId, args.tripId, args.rateeId, args.rateeName, args.rateeIsDriver)
                    }
                    RatingScreen(
                        viewModel = viewModel,
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
