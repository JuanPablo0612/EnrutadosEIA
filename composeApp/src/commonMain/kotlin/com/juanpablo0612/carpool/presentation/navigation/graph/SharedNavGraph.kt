package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.chat.ChatScreen
import com.juanpablo0612.carpool.presentation.chat.ChatViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.notification.NotificationsScreen
import com.juanpablo0612.carpool.presentation.notification.NotificationsViewModel
import com.juanpablo0612.carpool.presentation.place.add.AddPlaceAction
import com.juanpablo0612.carpool.presentation.place.add.AddPlaceScreen
import com.juanpablo0612.carpool.presentation.place.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerScreen
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerViewModel
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorMode
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.profile.ProfileScreen
import com.juanpablo0612.carpool.presentation.profile.ProfileViewModel
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileScreen
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileViewModel
import com.juanpablo0612.carpool.presentation.rating.RatingScreen
import com.juanpablo0612.carpool.presentation.rating.RatingViewModel
import com.juanpablo0612.carpool.presentation.safety.SafetyScreen
import com.juanpablo0612.carpool.presentation.safety.SafetyViewModel
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingScreen
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val MAP_PICK_RESULT_KEY = "map_pick_result"

/**
 * Role-agnostic routes reachable from both the driver and the passenger side of the app —
 * profile/account management, places, notifications, safety, chat, trip tracking, and
 * post-trip rating.
 */
fun NavGraphBuilder.sharedNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToMapPicker: (Double?, Double?) -> Unit,
    onCoordinatesPicked: (Double, Double) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSavedPlaces: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onDeleteAccountSuccess: () -> Unit,
    onRoleSwitched: (UserRole) -> Unit,
    onNavigateToDeepLink: (String) -> Unit,
    onNavigateToChat: (bookingId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
) {
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
            onBack = onNavigateBack,
            onPlaceSaved = onNavigateBack,
            onNavigateToMapPicker = onNavigateToMapPicker
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
            onBack = onNavigateBack,
            onNavigateToAddPlace = onNavigateToAddPlace,
        )
    }

    composable<Route.MapPicker> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.MapPicker>()
        val viewModel: MapPickerViewModel = koinViewModel {
            parametersOf(args.initialLatitude, args.initialLongitude)
        }
        MapPickerScreen(
            viewModel = viewModel,
            onCoordinatesPicked = onCoordinatesPicked,
            onBack = onNavigateBack,
        )
    }

    composable<Route.Profile> {
        val viewModel: ProfileViewModel = koinViewModel()
        ProfileScreen(
            viewModel = viewModel,
            onNavigateToRoutes = onNavigateToRoutes,
            onNavigateToVehicles = onNavigateToVehicles,
            onLogout = onLogout,
            onNavigateToEditProfile = onNavigateToEditProfile,
            // The list, not the creation form — the row is labelled "saved places".
            onNavigateToSavedPlaces = onNavigateToSavedPlaces,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToSafety = onNavigateToSafety,
            onDeleteAccountSuccess = onDeleteAccountSuccess,
            onRoleSwitched = onRoleSwitched
        )
    }

    composable<Route.EditProfile> {
        val viewModel: EditProfileViewModel = koinViewModel()
        EditProfileScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onSaved = onNavigateBack
        )
    }

    composable<Route.Notifications> {
        val viewModel: NotificationsViewModel = koinViewModel()
        NotificationsScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            // The ViewModel already parses a deep link off each notification; without
            // this the parsed destination was dropped and tapping only marked it read.
            onNavigateTo = onNavigateToDeepLink
        )
    }

    composable<Route.Safety> {
        val viewModel: SafetyViewModel = koinViewModel()
        SafetyScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.Chat> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.Chat>()
        val viewModel: ChatViewModel = koinViewModel {
            parametersOf(args.bookingId, args.otherPartyName, args.isReadOnly)
        }
        ChatScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.TripTracking> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.TripTracking>()
        val viewModel: TripTrackingViewModel = koinViewModel { parametersOf(args.tripId) }
        TripTrackingScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onNavigateToChat = onNavigateToChat,
            onTripCompleted = onNavigateBack
        )
    }

    composable<Route.PostTripRating> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.PostTripRating>()
        val viewModel: RatingViewModel = koinViewModel {
            parametersOf(args.bookingId, args.tripId, args.rateeId, args.rateeName, args.rateeIsDriver)
        }
        RatingScreen(
            viewModel = viewModel,
            onDismiss = onNavigateBack
        )
    }
}
