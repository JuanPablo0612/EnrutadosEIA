package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.home.HomeAction
import com.juanpablo0612.carpool.presentation.home.HomeError
import com.juanpablo0612.carpool.presentation.home.HomeUiState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_create_route_cta
import enrutadoseia.composeapp.generated.resources.home_no_route_subtitle
import enrutadoseia.composeapp.generated.resources.home_no_route_title
import enrutadoseia.composeapp.generated.resources.home_no_vehicle_subtitle
import enrutadoseia.composeapp.generated.resources.home_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.home_passenger_empty_subtitle
import enrutadoseia.composeapp.generated.resources.home_passenger_empty_title
import enrutadoseia.composeapp.generated.resources.home_register_vehicle_cta
import enrutadoseia.composeapp.generated.resources.home_search_trip_cta
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeDashboard(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    val now = remember { Clock.System.now().toEpochMilliseconds() }

    LazyColumn(
        contentPadding = PaddingValues(bottom = Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        val bookingActionError = (state.error as? HomeError.BookingAction)?.error
        if (bookingActionError != null) {
            item(key = "booking_action_error") {
                ErrorMessage(
                    message = stringResource(bookingActionError.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .clickable { onAction(HomeAction.DismissBookingActionError) },
                )
            }
        }

        state.user?.let { user ->
            item(key = "greeting") {
                GreetingBlock(user = user)
            }
        }

        // The next ride and the next drive, soonest first. Items are added conditionally so the
        // list's own item animation carries the change.
        val nextBooking = state.nextBooking
        val nextTrip = state.nextTrip
        val bookingFirst = nextBooking != null &&
            (nextTrip == null || nextBooking.departureTime <= nextTrip.departureTime)
        val bookingItem: () -> Unit = {
            if (nextBooking != null) {
                item(key = "next_booking") {
                    NextBookingCard(
                        booking = nextBooking,
                        now = now,
                        onTap = { onAction(HomeAction.OpenBooking(nextBooking.tripId)) },
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }
            }
        }
        val tripItem: () -> Unit = {
            if (nextTrip != null) {
                item(key = "next_trip") {
                    NextTripCard(
                        trip = nextTrip,
                        now = now,
                        onTap = { onAction(HomeAction.OpenTrip(nextTrip.id)) },
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }
            }
        }
        if (bookingFirst) {
            bookingItem()
            tripItem()
        } else {
            tripItem()
            bookingItem()
        }
        if (nextBooking == null && nextTrip == null) {
            item(key = "no_upcoming") {
                OnboardingBanner(
                    title = stringResource(Res.string.home_passenger_empty_title),
                    subtitle = stringResource(Res.string.home_passenger_empty_subtitle),
                    ctaLabel = stringResource(Res.string.home_search_trip_cta),
                    onCta = { onAction(HomeAction.SearchTrips) },
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }
        }

        // AnimatedVisibility owns the non-empty condition (no enclosing `if` on the same test),
        // so the pending-requests section can animate out when the list empties.
        item(key = "pending_requests") {
            AnimatedVisibility(
                visible = state.pendingRequests.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                PendingRequestsSection(
                    requests = state.pendingRequests,
                    onAccept = { onAction(HomeAction.AcceptRequest(it)) },
                    onReject = { onAction(HomeAction.OnRejectRequestClick(it)) },
                    onSeeAll = { onAction(HomeAction.OpenAllRequests) },
                    processingIds = state.processingBookingIds,
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }
        }

        item(key = "quick_actions") {
            QuickActionsGrid(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )
        }

        when {
            !state.hasVehicles -> item(key = "vehicle_cta") {
                OnboardingBanner(
                    title = stringResource(Res.string.home_no_vehicle_title),
                    subtitle = stringResource(Res.string.home_no_vehicle_subtitle),
                    ctaLabel = stringResource(Res.string.home_register_vehicle_cta),
                    onCta = { onAction(HomeAction.RegisterVehicle) },
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }
            !state.hasRoutes -> item(key = "route_cta") {
                OnboardingBanner(
                    title = stringResource(Res.string.home_no_route_title),
                    subtitle = stringResource(Res.string.home_no_route_subtitle),
                    ctaLabel = stringResource(Res.string.home_create_route_cta),
                    onCta = { onAction(HomeAction.CreateRoute) },
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }
        }

        // Shown from the first trip of the month, so new/low-volume drivers see their stats too.
        if (state.tripsThisMonth > 0) {
            item(key = "stats") {
                StatsSection(
                    tripsThisMonth = state.tripsThisMonth,
                    passengersThisMonth = state.passengersThisMonth,
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }
        }
    }
}
