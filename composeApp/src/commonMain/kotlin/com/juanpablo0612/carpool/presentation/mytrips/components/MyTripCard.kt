package com.juanpablo0612.carpool.presentation.mytrips.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.mytrips.MyTripItem
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsAction
import com.juanpablo0612.carpool.presentation.mytrips.canRate
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.ButtonPair
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.Pill
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_action_message_driver
import enrutadoseia.composeapp.generated.resources.booking_action_rate
import enrutadoseia.composeapp.generated.resources.cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_button
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_role_driving
import enrutadoseia.composeapp.generated.resources.home_role_riding
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.my_trips_gets_off_at
import enrutadoseia.composeapp.generated.resources.my_trips_gets_on_at
import enrutadoseia.composeapp.generated.resources.my_trips_more_actions
import enrutadoseia.composeapp.generated.resources.my_trips_with_driver
import enrutadoseia.composeapp.generated.resources.trip_action_start
import enrutadoseia.composeapp.generated.resources.trip_action_track
import enrutadoseia.composeapp.generated.resources.trip_action_view_passengers
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import enrutadoseia.composeapp.generated.resources.trip_seats_occupied
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * One trip in "Mis viajes", whichever side the user is on: the role, its state, when it leaves,
 * the route and, for a seat, who drives and where the passenger meets them. The card opens the
 * trip; its buttons are the next step for that trip right now, and rarer ones (cancelling) sit in
 * the overflow menu so they aren't tapped by accident.
 */
@Composable
internal fun MyTripCard(
    item: MyTripItem,
    isUpcoming: Boolean,
    isBusy: Boolean,
    onAction: (MyTripsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier, onClick = { onAction(MyTripsAction.OnItemClick(item)) }) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                RolePill(item)
                Spacer(modifier = Modifier.weight(1f))
                StatusSlot(item)
                OverflowMenu(item = item, isUpcoming = isUpcoming, enabled = !isBusy, onAction = onAction)
            }
            Text(text = formatTime(item.departureTime), style = MaterialTheme.typography.headlineSmall)
            when (item) {
                is MyTripItem.Driving -> RouteLineRow(origin = item.trip.origin.name, destination = item.trip.destination.name)
                is MyTripItem.Riding -> {
                    RouteLineRow(origin = item.booking.originName, destination = item.booking.destinationName)
                    DriverLine(item)
                }
            }
            Actions(item = item, isUpcoming = isUpcoming, isBusy = isBusy, onAction = onAction)
        }
    }
}

@Composable
private fun RolePill(item: MyTripItem) {
    when (item) {
        is MyTripItem.Driving -> Pill(
            text = stringResource(Res.string.home_role_driving),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            icon = vectorResource(Res.drawable.directions_car_24px),
        )
        is MyTripItem.Riding -> Pill(
            text = stringResource(Res.string.home_role_riding),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The one fact worth a glance: how full an active trip is, or the state of anything else. */
@Composable
private fun StatusSlot(item: MyTripItem) {
    when (item) {
        is MyTripItem.Driving -> if (item.trip.status == TripStatus.Active) {
            Text(
                text = stringResource(Res.string.trip_seats_occupied, item.trip.confirmedSeats, item.trip.seatCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            TripStatusBadge(status = item.trip.status)
        }
        is MyTripItem.Riding -> BookingStatusBadge(status = item.booking.status)
    }
}

@Composable
private fun DriverLine(item: MyTripItem.Riding) {
    val booking = item.booking
    val name = booking.driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        UserAvatar(name = name, photoUrl = booking.driver.photoUrl, size = 32.dp)
        Column {
            Text(
                text = stringResource(Res.string.my_trips_with_driver, name.substringBefore(' ')),
                style = MaterialTheme.typography.titleSmall,
            )
            booking.meetingStop?.let { stop ->
                Text(
                    text = stringResource(
                        if (stop.isDropoff) Res.string.my_trips_gets_off_at else Res.string.my_trips_gets_on_at,
                        stop.name,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun Actions(item: MyTripItem, isUpcoming: Boolean, isBusy: Boolean, onAction: (MyTripsAction) -> Unit) {
    val enabled = !isBusy
    when (item) {
        is MyTripItem.Driving -> {
            val trip = item.trip
            when {
                trip.status == TripStatus.InProgress -> PrimaryButton(
                    text = stringResource(Res.string.trip_action_track),
                    onClick = { onAction(MyTripsAction.OnContinueTrip(trip.id)) },
                    enabled = enabled,
                )
                isUpcoming -> ButtonPair(
                    spacing = Spacing.sm,
                    secondary = {
                        SecondaryButton(
                            text = stringResource(Res.string.trip_action_view_passengers),
                            onClick = { onAction(MyTripsAction.OnViewPassengers(trip.id)) },
                            enabled = enabled,
                            modifier = it,
                        )
                    },
                    primary = {
                        PrimaryButton(
                            text = stringResource(Res.string.trip_action_start),
                            onClick = { onAction(MyTripsAction.OnStartTrip(trip.id)) },
                            isLoading = isBusy,
                            modifier = it,
                        )
                    },
                )
                // A finished trip still leads to its passengers, where the driver rates them.
                trip.status != TripStatus.Cancelled -> SecondaryButton(
                    text = stringResource(Res.string.trip_action_view_passengers),
                    onClick = { onAction(MyTripsAction.OnViewPassengers(trip.id)) },
                )
            }
        }
        is MyTripItem.Riding -> {
            val status = item.booking.status
            when {
                isUpcoming && status == BookingStatus.Pending -> SecondaryButton(
                    text = stringResource(Res.string.cancel_pending_confirm_button),
                    onClick = { onAction(MyTripsAction.OnCancelBooking(item)) },
                    enabled = enabled,
                )
                isUpcoming && status == BookingStatus.Confirmed -> SecondaryButton(
                    text = stringResource(Res.string.booking_action_message_driver),
                    onClick = { onAction(MyTripsAction.OnMessageDriver(item, isReadOnly = false)) },
                    enabled = enabled,
                )
                !isUpcoming && item.canRate -> PrimaryButton(
                    text = stringResource(Res.string.booking_action_rate),
                    onClick = { onAction(MyTripsAction.OnRateDriver(item)) },
                )
            }
        }
    }
}

/**
 * Cancelling lives here rather than as a button: it is rare, it notifies other people, and the
 * menu keeps it one deliberate step away. Hidden when there is nothing to cancel.
 */
@Composable
private fun OverflowMenu(item: MyTripItem, isUpcoming: Boolean, enabled: Boolean, onAction: (MyTripsAction) -> Unit) {
    val cancel: Pair<String, MyTripsAction>? = when {
        !isUpcoming -> null
        item is MyTripItem.Driving && item.trip.status == TripStatus.Active ->
            stringResource(Res.string.trip_cancel_confirm_button) to MyTripsAction.OnCancelTrip(item.tripId)
        item is MyTripItem.Riding && item.booking.status == BookingStatus.Confirmed ->
            stringResource(Res.string.cancel_confirm_button) to MyTripsAction.OnCancelBooking(item)
        else -> null
    }
    if (cancel == null) return
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, enabled = enabled) {
            Icon(
                imageVector = vectorResource(Res.drawable.more_vert_24px),
                contentDescription = stringResource(Res.string.my_trips_more_actions),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(cancel.first, color = MaterialTheme.colorScheme.error) },
                onClick = {
                    expanded = false
                    onAction(cancel.second)
                },
            )
        }
    }
}
