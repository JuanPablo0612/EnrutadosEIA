package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsTab
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.driverNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.mainNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.rootNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.sharedNavGraph
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val topLevelItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.SearchTrips,
    BottomNavItem.MyTrips,
    BottomNavItem.Profile,
)

private val topLevelRouteClasses = topLevelItems.map { it.route::class }

/**
 * Switches to a bottom-bar destination. Every tab's stack is saved when leaving and restored when
 * coming back; [Route.Home] is the root, so back from any tab root returns to Inicio.
 *
 * Top-level destinations must always be reached through here, never pushed: in this flat graph a
 * pushed tab root would end up inside another tab's saved stack.
 */
internal fun NavHostController.navigateToTopLevel(route: Route, restoreState: Boolean = true) {
    navigate(route) {
        popUpTo<Route.Home> { saveState = true }
        launchSingleTop = true
        this.restoreState = restoreState
    }
}

/**
 * Opens a persisted notification deep link. Tab destinations switch tabs; anything else is pushed
 * so back returns to where the user tapped the notification.
 */
internal fun NavHostController.navigateToNotificationDeepLink(link: String) {
    val route = link.toRouteOrNull() ?: return
    if (route::class in topLevelRouteClasses) {
        navigateToTopLevel(route, restoreState = false)
    } else {
        navigate(route)
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val userSession = koinInject<UserSession>()
    val authRepository = koinInject<AuthRepository>()
    val notificationRepository = koinInject<NotificationRepository>()
    val bookingRepository = koinInject<BookingRepository>()
    val scope = rememberCoroutineScope()
    val currentUser by userSession.user.collectAsState()

    // UserSession lives in memory only. After process death the NavHost restores its back stack
    // without going through Splash, so reload the signed-in user here.
    LaunchedEffect(Unit) {
        if (userSession.user.value == null && authRepository.getCurrentUserId() != null) {
            authRepository.getCurrentUser().onSuccess(userSession::setUser)
        }
    }

    // The only unread-item signal anywhere in the nav chrome — otherwise a user has to drill into
    // Profile > Notifications just to find out whether anything is new.
    val unreadNotificationCount by produceState(initialValue = 0, currentUser?.id) {
        val userId = currentUser?.id
        if (userId.isNullOrBlank()) {
            value = 0
        } else {
            notificationRepository.getNotifications(userId).collect { notifications ->
                value = notifications.count { !it.isRead }
            }
        }
    }

    // Seat requests waiting on the user as a driver, badged on "Mis viajes".
    val pendingRequestCount by produceState(initialValue = 0, currentUser?.id) {
        val userId = currentUser?.id
        if (userId.isNullOrBlank()) {
            value = 0
        } else {
            bookingRepository.getDriverBookingRequests(userId)
                .map { bookings -> bookings.count { it.status == BookingStatus.Pending } }
                .catch { emit(0) }
                .collect { value = it }
        }
    }

    val navBackstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackstackEntry?.destination
    val showBottomBar = topLevelItems.any { currentDestination?.hasRoute(it.route::class) == true }

    // A tapped push waits until a signed-in tab is on screen, so Splash's own navigation on a cold
    // start can't wipe the target, then opens only if it was meant for the current user.
    val pendingDeepLinks = koinInject<PendingDeepLinks>()
    val pendingDeepLink by pendingDeepLinks.link.collectAsState()
    LaunchedEffect(pendingDeepLink, showBottomBar, currentUser?.id) {
        val pending = pendingDeepLink ?: return@LaunchedEffect
        val userId = currentUser?.id
        if (!showBottomBar || userId == null) return@LaunchedEffect
        if (userId == pending.recipientId) {
            navController.navigateToNotificationDeepLink(pending.deepLink)
            pending.notificationId?.let { notificationRepository.markRead(userId, it) }
        }
        pendingDeepLinks.consume()
    }

    fun enterApp(user: User) {
        userSession.setUser(user)
        navController.navigate(Route.Home) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Today's publish path: pick one of your routes, then fill in the trip.
    val onPublishTrip: () -> Unit = {
        navController.navigate(Route.RoutesList) { launchSingleTop = true }
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

    CarpoolTheme {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        items = topLevelItems,
                        badgeCounts = mapOf(
                            Route.Profile::class to unreadNotificationCount,
                            Route.MyTrips::class to pendingRequestCount,
                        ),
                        onNavigate = { route -> navController.navigateToTopLevel(route as Route) }
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Splash,
                // Directional slides (rather than NavHost's default fade) so forward and back
                // navigation give a cue about depth. Declared once here rather than
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
                    onSplashNavigateToHome = ::enterApp,
                    onOnboardingNavigateToApp = {
                        navController.navigate(Route.Splash) {
                            popUpTo<Route.Onboarding> { inclusive = true }
                        }
                    },
                )

                authNavGraph(
                    onAuthSuccess = ::enterApp,
                    onNavigateToRegister = { navController.navigate(Route.Register) },
                    onNavigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                    onNavigateToEmailVerification = { navController.navigate(Route.EmailVerification) },
                    onNavigateBack = { navController.popBackStack() },
                    canNavigateBack = { navController.previousBackStackEntry != null }
                )

                mainNavGraph(
                    pendingRequestCount = { pendingRequestCount },
                    onNavigateToProfile = { navController.navigateToTopLevel(Route.Profile) },
                    onPublishTrip = onPublishTrip,
                    onNavigateToCreateRoute = { navController.navigate(Route.CreateRoute) },
                    onNavigateToRegisterVehicle = { navController.navigate(Route.RegisterVehicle()) },
                    onNavigateToRoutesList = { navController.navigate(Route.RoutesList) },
                    onNavigateToSavedPlaces = { navController.navigate(Route.SavedPlaces) },
                    onNavigateToSearchTrips = { navController.navigateToTopLevel(Route.SearchTrips) },
                    onNavigateToMyTrips = { tab ->
                        if (tab == null) {
                            navController.navigateToTopLevel(Route.MyTrips())
                        } else {
                            navController.navigateToTopLevel(Route.MyTrips(tab), restoreState = false)
                        }
                    },
                    onNavigateToDriverBookingRequests = { navController.navigate(Route.DriverBookingRequests) },
                    onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onBookingCreated = {
                        // Leave the booked trip out of the Search tab's saved stack.
                        navController.popBackStack<Route.TripDetailPassenger>(inclusive = true)
                        navController.navigateToTopLevel(Route.MyTrips(MyTripsTab.Passenger), restoreState = false)
                    },
                    onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                    onNavigateToPassengers = { tripId -> navController.navigate(Route.TripPassengers(tripId)) },
                    onNavigateToRating = { bookingId, tripId, rateeId, rateeName ->
                        // Rating from "Como pasajero": the ratee is always the driver, which
                        // selects the "clean car / safe driving" chip set.
                        navController.navigate(
                            Route.PostTripRating(bookingId, tripId, rateeId, rateeName, rateeIsDriver = true)
                        )
                    },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateToChat = { bookingId, tripId, otherPartyName, isReadOnly ->
                        navController.navigate(Route.Chat(bookingId, tripId, otherPartyName, isReadOnly))
                    },
                    onNavigateBack = { navController.popBackStack() },
                )

                driverNavGraph(
                    onNavigateToCreateRoute = { navController.navigate(Route.CreateRoute) },
                    onNavigateToRegisterVehicle = { navController.navigate(Route.RegisterVehicle()) },
                    onNavigateToEditVehicle = { id -> navController.navigate(Route.RegisterVehicle(id)) },
                    onNavigateToRouteDetail = { routeId -> navController.navigate(Route.RouteDetail(routeId)) },
                    onNavigateToCreateTrip = { routeId -> navController.navigate(Route.CreateTrip(routeId)) },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateToRoutesList = { navController.navigate(Route.RoutesList) },
                    onNavigateToCommunityRoutes = { navController.navigate(Route.CommunityRoutes) },
                    onNavigateToVehiclesList = { navController.navigate(Route.VehiclesList) },
                    onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                    onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                    onNavigateToPassengers = { tripId -> navController.navigate(Route.TripPassengers(tripId)) },
                    onNavigateToPassengerProfile = { userId -> navController.navigate(Route.PassengerProfile(userId)) },
                    onNavigateToRating = { bookingId, tripId, rateeId, rateeName ->
                        // Rating from a trip the user drove: the ratee is always the passenger.
                        navController.navigate(
                            Route.PostTripRating(bookingId, tripId, rateeId, rateeName, rateeIsDriver = false)
                        )
                    },
                    onNavigateToChat = { bookingId, tripId, otherPartyName, isReadOnly ->
                        navController.navigate(Route.Chat(bookingId, tripId, otherPartyName, isReadOnly))
                    },
                    onNavigateBack = { navController.popBackStack() },
                )

                sharedNavGraph(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMapPicker = { lat, lon ->
                        val mapPicker = if (lat != null && lon != null) {
                            Route.MapPicker(lat, lon)
                        } else {
                            Route.MapPicker()
                        }
                        navController.navigate(mapPicker)
                    },
                    onCoordinatesPicked = { lat, lon, placeName ->
                        navController.popWithMapPickResult(MapPickResult(lat, lon, placeName))
                    },
                    onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                    onNavigateToRoutes = { navController.navigate(Route.RoutesList) },
                    onNavigateToVehicles = { navController.navigate(Route.VehiclesList) },
                    onLogout = onLogout,
                    onNavigateToEditProfile = { navController.navigate(Route.EditProfile) },
                    // The list, not the creation form — the row is labelled "saved places".
                    onNavigateToSavedPlaces = { navController.navigate(Route.SavedPlaces) },
                    onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                    onDeleteAccountSuccess = {
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToDeepLink = navController::navigateToNotificationDeepLink,
                    onNavigateToChat = { bookingId, tripId, otherPartyName, isReadOnly ->
                        navController.navigate(Route.Chat(bookingId, tripId, otherPartyName, isReadOnly))
                    }
                )
            }
        }
    }
}
