package com.juanpablo0612.carpool.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.components.OfflineBanner
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import kotlin.time.Clock

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
        item(key = "offline_banner") {
            OfflineBanner(isOffline = state.isOffline)
        }

        state.user?.let { user ->
            item(key = "greeting") {
                GreetingBlock(user = user)
            }
        }

        if (state.role == UserRole.Driver) {
            state.nextTrip?.let { trip ->
                item(key = "next_trip") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                    ) {
                        NextTripCard(
                            trip = trip,
                            now = now,
                            onTap = { onAction(HomeAction.OpenTrip(trip.id)) },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }
            }
        } else {
            state.nextBooking?.let { booking ->
                item(key = "next_booking") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                    ) {
                        NextBookingCard(
                            booking = booking,
                            now = now,
                            onTap = { onAction(HomeAction.OpenBooking(booking.tripId)) },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }
            }
        }

        if (state.role == UserRole.Driver && state.pendingRequests.isNotEmpty()) {
            item(key = "pending_requests") {
                AnimatedVisibility(
                    visible = state.pendingRequests.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                ) {
                    PendingRequestsSection(
                        requests = state.pendingRequests,
                        onAccept = { onAction(HomeAction.AcceptRequest(it)) },
                        onReject = { onAction(HomeAction.RejectRequest(it)) },
                        onSeeAll = { onAction(HomeAction.OpenAllRequests) },
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

        if (state.tripsThisMonth >= 3) {
            item(key = "stats") {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                ) {
                    StatsSection(
                        tripsThisMonth = state.tripsThisMonth,
                        passengersThisMonth = state.passengersThisMonth,
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }
            }
        }
    }
}
