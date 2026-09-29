package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerUiState
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_request_note
import enrutadoseia.composeapp.generated.resources.confirm_request_title
import enrutadoseia.composeapp.generated.resources.confirm_row_contribution
import enrutadoseia.composeapp.generated.resources.confirm_row_driver
import enrutadoseia.composeapp.generated.resources.confirm_row_get_off
import enrutadoseia.composeapp.generated.resources.confirm_row_get_on
import enrutadoseia.composeapp.generated.resources.confirm_row_route
import enrutadoseia.composeapp.generated.resources.info_24px
import enrutadoseia.composeapp.generated.resources.passenger_message_counter
import enrutadoseia.composeapp.generated.resources.passenger_message_label
import enrutadoseia.composeapp.generated.resources.request_quick_luggage
import enrutadoseia.composeapp.generated.resources.request_quick_on_time
import enrutadoseia.composeapp.generated.resources.route_from_to
import enrutadoseia.composeapp.generated.resources.send_request_button
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The last step before asking for a seat: a summary of what the passenger is committing to, an
 * optional note for the driver (with quick phrases), and what happens next.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConfirmRequestSheet(
    state: RouteDetailPassengerUiState,
    sheetState: SheetState,
    onAction: (RouteDetailPassengerAction) -> Unit,
) {
    val trip = state.trip ?: return
    ModalBottomSheet(
        onDismissRequest = { onAction(RouteDetailPassengerAction.OnDismissConfirmSheet) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.confirm_request_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )

            Column {
                SummaryRow(
                    label = stringResource(Res.string.confirm_row_driver),
                    value = trip.driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) },
                    isFirst = true,
                )
                val path = listOf(trip.origin) + trip.waypoints + trip.destination
                val meeting = state.meetingStop?.let { stop -> path.getOrNull(stop.pathIndex)?.let { stop to it } }
                if (meeting != null) {
                    SummaryRow(
                        label = stringResource(if (meeting.first.isDropoff) Res.string.confirm_row_get_off else Res.string.confirm_row_get_on),
                        value = meeting.second.name,
                    )
                } else {
                    SummaryRow(
                        label = stringResource(Res.string.confirm_row_route),
                        value = stringResource(Res.string.route_from_to, trip.origin.name, trip.destination.name),
                    )
                }
                SummaryRow(
                    label = stringResource(Res.string.confirm_row_contribution),
                    value = contributionLabel(trip.contributionPerPassenger),
                )
            }

            MessageField(message = state.passengerMessage, onAction = onAction)

            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.info_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(text = stringResource(Res.string.confirm_request_note), style = MaterialTheme.typography.bodyMedium)
                }
            }

            state.error?.let { ErrorMessage(message = stringResource(it.asStringResource())) }

            PrimaryButton(
                text = stringResource(Res.string.send_request_button),
                onClick = { onAction(RouteDetailPassengerAction.OnConfirmBookingRequest) },
                isLoading = state.isBooking,
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isFirst: Boolean = false) {
    if (!isFirst) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MessageField(message: String, onAction: (RouteDetailPassengerAction) -> Unit) {
    val quickMessages = listOf(
        stringResource(Res.string.request_quick_luggage),
        stringResource(Res.string.request_quick_on_time),
    )
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        CarpoolTextField(
            value = message,
            onValueChange = { onAction(RouteDetailPassengerAction.OnPassengerMessageChanged(it)) },
            label = stringResource(Res.string.passenger_message_label),
            placeholder = "",
            singleLine = false,
            minLines = 2,
            supportingText = {
                Text(
                    text = stringResource(Res.string.passenger_message_counter, message.length),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            },
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            quickMessages.forEach { text ->
                SuggestionChip(
                    onClick = { onAction(RouteDetailPassengerAction.OnQuickMessage(text)) },
                    label = { Text(text) },
                )
            }
        }
    }
}
