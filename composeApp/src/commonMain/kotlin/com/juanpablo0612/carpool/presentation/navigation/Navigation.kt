package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.presentation.navigation.graph.authNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.driverNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.mainNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.rootNavGraph
import com.juanpablo0612.carpool.presentation.navigation.graph.sharedNavGraph
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
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

    val onPublishTrip: () -> Unit = {
        navController.navigate(Route.PublishTrip()) { launchSingleTop = true }
    }

    val onLogout: () -> Unit = {
        scope.launch {
            authRepository.logout()
            userSession.clearSession()
            navController.navigate(Route.Entry) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CarpoolTheme {
        // Material's recommendation for the window: a bottom bar on phones (in landscape too), a
        // rail on unfolded foldables and tablets.
        val suiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        val isRail = suiteType == NavigationSuiteType.NavigationRail
        val suiteState = rememberNavigationSuiteScaffoldState(
            if (showBottomBar) NavigationSuiteScaffoldValue.Visible else NavigationSuiteScaffoldValue.Hidden
        )
        // Slides the bar or rail in and out instead of popping it when entering or leaving a tab.
        LaunchedEffect(showBottomBar) {
            if (showBottomBar) suiteState.show() else suiteState.hide()
        }
        val onNavigateToTab: (Any) -> Unit = { route -> navController.navigateToTopLevel(route as Route) }
        // The insets the bar or rail already pads itself by, consumed so each screen's own
        // Scaffold doesn't apply them a second time. Nothing as soon as it starts sliding away, so
        // a destination without it lays out its final insets from the first frame.
        val suiteInsets = when {
            suiteState.targetValue == NavigationSuiteScaffoldValue.Hidden -> WindowInsets(0, 0, 0, 0)
            isRail -> ScreenInsets.only(WindowInsetsSides.Start)
            else -> BottomBarInsets.only(WindowInsetsSides.Bottom)
        }

        Surface(color = MaterialTheme.colorScheme.background) {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    if (isRail) {
                        NavigationRailBar(
                            currentDestination = currentDestination,
                            items = topLevelItems,
                            onNavigate = onNavigateToTab,
                        )
                    } else {
                        BottomNavigationBar(
                            currentDestination = currentDestination,
                            items = topLevelItems,
                            onNavigate = onNavigateToTab,
                        )
                    }
                },
                navigationSuiteType = suiteType,
                state = suiteState,
            ) {
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
                    modifier = modifier
                        .fillMaxSize()
                        .consumeWindowInsets(suiteInsets)
                ) {
                    rootNavGraph(
                        onSplashNavigateToAuth = {
                            navController.navigate(Route.Entry) {
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
                        onNavigateToLogin = { navController.navigate(Route.Login) },
                        onSwitchToLogin = {
                            // Register can be reached from Entry or from Login: replace it, so
                            // back from Login still returns to Entry either way.
                            navController.navigate(Route.Login) {
                                popUpTo<Route.Register> { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToRegister = { navController.navigate(Route.Register) },
                        onNavigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                        onNavigateToEmailVerification = { navController.navigate(Route.EmailVerification) },
                        onSignUpAgain = {
                            // The verification screen can be the only entry (reached from Splash),
                            // so rebuild the auth stack rather than popping: Entry below Register.
                            navController.navigate(Route.Entry) { popUpTo(0) { inclusive = true } }
                            navController.navigate(Route.Register)
                        },
                        onNavigateBack = { navController.popBackStack() },
                        canNavigateBack = { navController.previousBackStackEntry != null }
                    )

                    mainNavGraph(
                        onNavigateToProfile = { navController.navigateToTopLevel(Route.Profile) },
                        onPublishTrip = onPublishTrip,
                        onNavigateToRegisterVehicle = { navController.navigate(Route.RegisterVehicle()) },
                        onNavigateToRoutesList = { navController.navigate(Route.RoutesList) },
                        onNavigateToSearchTrips = { shortcut ->
                            if (shortcut == null) {
                                navController.navigateToTopLevel(Route.SearchTrips())
                            } else {
                                // A fresh entry, not the saved one, so the search opens on the shortcut.
                                navController.navigateToTopLevel(
                                    Route.SearchTrips(
                                        campusId = shortcut.campus.id,
                                        fromCampus = shortcut.direction == CampusDirection.FromCampus,
                                    ),
                                    restoreState = false,
                                )
                            }
                        },
                        onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                        onNavigateToDriverBookingRequests = { navController.navigate(Route.DriverBookingRequests) },
                        onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                        onNavigateToSearchResult = { tripId, meetingStop ->
                            navController.navigate(
                                Route.TripDetailPassenger(
                                    tripId = tripId,
                                    meetingStopIndex = meetingStop?.pathIndex,
                                    meetingIsDropoff = meetingStop?.isDropoff ?: false,
                                )
                            )
                        },
                        onNavigateToUserProfile = { userId -> navController.navigate(Route.PassengerProfile(userId)) },
                        onBookingCreated = {
                            // Leave the booked trip out of the Search tab's saved stack.
                            navController.popBackStack<Route.TripDetailPassenger>(inclusive = true)
                            navController.navigateToTopLevel(Route.MyTrips, restoreState = false)
                        },
                        onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                        onNavigateToPassengers = { tripId -> navController.navigate(Route.TripPassengers(tripId)) },
                        onNavigateToEditTrip = { tripId -> navController.navigate(Route.EditTrip(tripId)) },
                        onNavigateToRating = { target -> navController.navigate(target.toRatingRoute()) },
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
                        onNavigateToPublishTrip = { routeId -> navController.navigate(Route.PublishTrip(routeId)) },
                        onNavigateToAddPlace = { navController.navigate(Route.AddPlace) },
                        onNavigateToRoutesList = { navController.navigate(Route.RoutesList) },
                        onNavigateToVehiclesList = { navController.navigate(Route.VehiclesList) },
                        onTripPublished = {
                            // Drop the finished form first so it isn't saved into the Inicio tab's
                            // stack, then land on the trips you drive.
                            navController.popBackStack<Route.PublishTrip>(inclusive = true)
                            navController.popBackStack<Route.PublishWeek>(inclusive = true)
                            navController.navigateToTopLevel(Route.MyTrips, restoreState = false)
                        },
                        onNavigateToPublishWeek = { routeId -> navController.navigate(Route.PublishWeek(routeId)) },
                        onNavigateToTripDetail = { tripId -> navController.navigate(Route.TripDetailPassenger(tripId)) },
                        onNavigateToTripTracking = { tripId -> navController.navigate(Route.TripTracking(tripId)) },
                        onNavigateToPassengers = { tripId -> navController.navigate(Route.TripPassengers(tripId)) },
                        onNavigateToEditTrip = { tripId -> navController.navigate(Route.EditTrip(tripId)) },
                        onNavigateToPassengerProfile = { userId -> navController.navigate(Route.PassengerProfile(userId)) },
                        onNavigateToRating = { target -> navController.navigate(target.toRatingRoute()) },
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
                            navController.navigate(Route.Entry) {
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
}
