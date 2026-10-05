package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerUiState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.book_closed_caption
import enrutadoseia.composeapp.generated.resources.book_request_button
import enrutadoseia.composeapp.generated.resources.book_search_another_trip
import enrutadoseia.composeapp.generated.resources.book_request_sent
import enrutadoseia.composeapp.generated.resources.book_request_subtext
import enrutadoseia.composeapp.generated.resources.booking_request_sent_notice
import enrutadoseia.composeapp.generated.resources.no_seats_available
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The bar pinned under the trip: the price and the one action a passenger takes. It stays put
 * while the details scroll, so asking for a seat is always one tap away. Once a request is sent
 * or the trip is full, the button disables and says why; once the trip has closed, the bar offers
 * another search instead (the reason is shown above the details).
 */
@Composable
internal fun BookingBar(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.trip == null) return
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = modifier) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                modifier = Modifier
                    .windowInsetsPadding(BottomBarInsets)
                    // The bar spans the window; its content lines up with the details above it.
                    .centeredContent(ContentWidth.list)
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                if (!state.isBookable) {
                    ClosedBookingContent(onAction = onAction)
                } else {
                    OpenBookingContent(state = state, onAction = onAction)
                }
            }
        }
    }
}

/** A closed trip has nothing to request: say so quietly and lead back to Buscar. */
@Composable
private fun ClosedBookingContent(onAction: (RouteDetailPassengerAction) -> Unit) {
    Text(
        text = stringResource(Res.string.book_closed_caption),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    PrimaryButton(
        text = stringResource(Res.string.book_search_another_trip),
        onClick = { onAction(RouteDetailPassengerAction.OnSearchAnotherTrip) },
        leadingIcon = vectorResource(Res.drawable.search_24px),
    )
}

@Composable
private fun OpenBookingContent(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit,
) {
    val trip = state.trip ?: return
    if (state.bookingRequestSent) {
        SuccessMessage(message = stringResource(Res.string.booking_request_sent_notice))
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        // Weighted like the button: an unweighted column is measured first and its
        // one-line subtext would take the whole row, squeezing the button's label.
        Column(modifier = Modifier.weight(1f)) {
            Text(text = contributionLabel(trip.contributionPerPassenger), style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(Res.string.book_request_subtext),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        PrimaryButton(
            text = stringResource(
                when {
                    state.alreadyRequested -> Res.string.book_request_sent
                    state.availableSeats <= 0 -> Res.string.no_seats_available
                    else -> Res.string.book_request_button
                }
            ),
            onClick = { onAction(RouteDetailPassengerAction.OnOpenConfirmSheet) },
            enabled = !state.alreadyRequested && state.availableSeats > 0,
            modifier = Modifier.weight(1f),
        )
    }
}
