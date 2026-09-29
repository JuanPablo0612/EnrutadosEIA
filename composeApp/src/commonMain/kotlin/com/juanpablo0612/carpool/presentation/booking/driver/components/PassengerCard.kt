package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_action_rate
import enrutadoseia.composeapp.generated.resources.chat_24px
import enrutadoseia.composeapp.generated.resources.confirmed_booking_cancel_button
import enrutadoseia.composeapp.generated.resources.confirmed_booking_message_button
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.passenger_more_actions
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * A passenger with a confirmed seat: who they are, where they meet the driver, and the next
 * step — a message before the trip, a rating after it. Taking the seat back is rare and notifies
 * the passenger, so it sits in the overflow menu, and only while the trip hasn't started.
 */
@Composable
internal fun PassengerCard(
    booking: Booking,
    isBusy: Boolean,
    canCancel: Boolean,
    onMessage: () -> Unit,
    /** Offered once the trip is over; null before. */
    onRate: (() -> Unit)?,
    onDecision: (BookingDecisionAction) -> Unit,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            PassengerHeader(booking = booking, onViewProfile = onViewProfile) {
                when {
                    isBusy -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    canCancel -> CancelMenu(onCancel = { onDecision(BookingDecisionAction.Cancel(booking)) })
                }
            }
            booking.meetingStop?.let { MeetingStopLine(stop = it) }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                SecondaryButton(
                    text = stringResource(Res.string.confirmed_booking_message_button),
                    onClick = onMessage,
                    leadingIcon = vectorResource(Res.drawable.chat_24px),
                    modifier = Modifier.weight(1f),
                )
                if (onRate != null) {
                    PrimaryButton(
                        text = stringResource(Res.string.booking_action_rate),
                        onClick = onRate,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CancelMenu(onCancel: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = vectorResource(Res.drawable.more_vert_24px),
                contentDescription = stringResource(Res.string.passenger_more_actions),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.confirmed_booking_cancel_button),
                        color = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = {
                    expanded = false
                    onCancel()
                },
            )
        }
    }
}
