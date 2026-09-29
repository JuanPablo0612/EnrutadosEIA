package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerUiState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.book_request_button
import enrutadoseia.composeapp.generated.resources.book_request_sent
import enrutadoseia.composeapp.generated.resources.book_request_subtext
import enrutadoseia.composeapp.generated.resources.booking_request_sent_notice
import enrutadoseia.composeapp.generated.resources.no_seats_available
import org.jetbrains.compose.resources.stringResource

/**
 * The bar pinned under the trip: the price and the one action a passenger takes. It stays put
 * while the details scroll, so asking for a seat is always one tap away. Once a request is sent
 * or the trip is full the button disables and says why.
 */
@Composable
internal fun BookingBar(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trip = state.trip ?: return
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = modifier) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                if (state.bookingRequestSent) {
                    SuccessMessage(message = stringResource(Res.string.booking_request_sent_notice))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                    Column {
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
        }
    }
}
