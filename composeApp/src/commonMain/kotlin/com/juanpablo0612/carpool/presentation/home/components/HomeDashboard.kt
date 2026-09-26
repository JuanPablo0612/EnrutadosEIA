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
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.home.HomeAction
import com.juanpablo0612.carpool.presentation.home.HomeError
import com.juanpablo0612.carpool.presentation.home.HomeUiState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
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

        // These items are added conditionally, so the list's own item animation carries the
        // change — an AnimatedVisibility(visible = true) wrapper would never play an exit.
        if (state.role == UserRole.Driver) {
            state.nextTrip?.let { trip ->
                item(key = "next_trip") {
                    NextTripCard(
                        trip = trip,
                        now = now,
                        onTap = { onAction(HomeAction.OpenTrip(trip.id)) },
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }
            }
        } else {
            state.nextBooking?.let { booking ->
                item(key = "next_booking") {
                    NextBookingCard(
                        booking = booking,
                        now = now,
                        onTap = { onAction(HomeAction.OpenBooking(booking.tripId)) },
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }
            }
        }

        // AnimatedVisibility owns the non-empty condition (no enclosing `if` on the same test),
        // so the pending-requests section can animate out when the list empties.
        if (state.role == UserRole.Driver) {
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
        }

        item(key = "quick_actions") {
            QuickActionsGrid(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )
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
