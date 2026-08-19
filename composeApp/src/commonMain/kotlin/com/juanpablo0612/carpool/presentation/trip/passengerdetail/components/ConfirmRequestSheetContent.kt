package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.route.search.components.formatEpochShort
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerUiState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.confirm_request_title
import enrutadoseia.composeapp.generated.resources.passenger_message_counter
import enrutadoseia.composeapp.generated.resources.passenger_message_label
import enrutadoseia.composeapp.generated.resources.send_request_button
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConfirmRequestSheetContent(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .navigationBarsPadding()
            .padding(bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = stringResource(Res.string.confirm_request_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        state.trip?.let { trip ->
            Text(
                text = "${trip.origin.name} → ${trip.destination.name}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = formatEpochShort(trip.departureTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = state.passengerMessage,
            onValueChange = { onAction(RouteDetailPassengerAction.OnPassengerMessageChanged(it)) },
            label = { Text(stringResource(Res.string.passenger_message_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            // OutlinedTextField's maxLines has no overflow parameter (Text-only API); typed input
            // naturally wraps rather than clipping mid-glyph, so this maxLines is unaffected by
            // Task 2's Ellipsis fix.
            maxLines = 4,
            supportingText = {
                Text(
                    text = stringResource(Res.string.passenger_message_counter, state.passengerMessage.length),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            OutlinedButton(
                onClick = { onAction(RouteDetailPassengerAction.OnDismissConfirmSheet) },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(Res.string.cancel_button))
            }
            Button(
                onClick = { onAction(RouteDetailPassengerAction.OnConfirmBookingRequest) },
                enabled = !state.isBooking,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(Res.string.send_request_button))
                }
            }
        }
    }
}
