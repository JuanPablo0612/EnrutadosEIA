package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerUiState
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.chat_24px
import enrutadoseia.composeapp.generated.resources.confirm_request_add_message
import enrutadoseia.composeapp.generated.resources.confirm_request_add_message_generic
import enrutadoseia.composeapp.generated.resources.confirm_request_driver_route
import enrutadoseia.composeapp.generated.resources.confirm_request_note
import enrutadoseia.composeapp.generated.resources.confirm_request_title
import enrutadoseia.composeapp.generated.resources.confirm_row_contribution
import enrutadoseia.composeapp.generated.resources.confirm_row_get_off
import enrutadoseia.composeapp.generated.resources.confirm_row_get_on
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
 * The last step before asking for a seat, kept short: where the passenger boards and what they
 * contribute up front, driver and route on one line, an optional note for the driver folded away
 * until asked for, and a one-line reminder of what happens next.
 *
 * The sheet's default content insets include the keyboard, so the expanded message field stays
 * above it and the scrolling column brings the focused field into view.
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

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                EssentialTiles(trip = trip, meetingStop = state.meetingStop)
                val driverName = trip.driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) }
                Text(
                    text = stringResource(
                        Res.string.confirm_request_driver_route,
                        driverName,
                        stringResource(Res.string.route_from_to, trip.origin.name, trip.destination.name),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            CollapsibleMessage(
                driverFirstName = trip.driver.name.trim().substringBefore(' '),
                message = state.passengerMessage,
                onAction = onAction,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.info_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(Res.string.confirm_request_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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

/** Where the passenger meets the car and what they pay, side by side and equally tall. */
@Composable
private fun EssentialTiles(trip: Trip, meetingStop: TripMeetingStop?) {
    val path = listOf(trip.origin) + trip.waypoints + trip.destination
    val meetingPlace = meetingStop?.let { path.getOrNull(it.pathIndex) }
    // Without a matched stop the passenger boards where the trip starts.
    val isDropoff = meetingPlace != null && meetingStop?.isDropoff == true
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        EssentialTile(
            label = stringResource(if (isDropoff) Res.string.confirm_row_get_off else Res.string.confirm_row_get_on),
            value = (meetingPlace ?: trip.origin).name,
            modifier = Modifier.weight(1f),
        )
        EssentialTile(
            label = stringResource(Res.string.confirm_row_contribution),
            value = contributionLabel(trip.contributionPerPassenger),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun EssentialTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Most requests go without a note, so the field starts folded behind one row and opens in place.
 * It starts open when a message is already written, so nothing typed is hidden.
 */
@Composable
private fun CollapsibleMessage(
    driverFirstName: String,
    message: String,
    onAction: (RouteDetailPassengerAction) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(message.isNotBlank()) }
    Column {
        AnimatedVisibility(
            visible = !expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Surface(
                onClick = { expanded = true },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.heightIn(min = 48.dp).padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.chat_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = if (driverFirstName.isNotBlank()) {
                            stringResource(Res.string.confirm_request_add_message, driverFirstName)
                        } else {
                            stringResource(Res.string.confirm_request_add_message_generic)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            MessageField(message = message, onAction = onAction)
        }
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
