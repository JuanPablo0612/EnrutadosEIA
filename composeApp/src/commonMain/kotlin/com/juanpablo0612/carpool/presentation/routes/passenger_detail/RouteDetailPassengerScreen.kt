package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.presentation.bookings.asStringResource
import com.juanpablo0612.carpool.presentation.routes.passenger_detail.components.StopsTimeline
import com.juanpablo0612.carpool.presentation.routes.search.formatEpochShort
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.available_seats_label
import enrutadoseia.composeapp.generated.resources.book_request_button
import enrutadoseia.composeapp.generated.resources.book_request_sent
import enrutadoseia.composeapp.generated.resources.book_request_subtext
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.confirm_request_title
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.no_seats_available
import enrutadoseia.composeapp.generated.resources.passenger_message_counter
import enrutadoseia.composeapp.generated.resources.passenger_message_label
import enrutadoseia.composeapp.generated.resources.route_detail_passenger_title
import enrutadoseia.composeapp.generated.resources.send_request_button
import enrutadoseia.composeapp.generated.resources.trip_contribution_free
import enrutadoseia.composeapp.generated.resources.trip_contribution_label
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import enrutadoseia.composeapp.generated.resources.trip_driver_section
import enrutadoseia.composeapp.generated.resources.trip_message_driver_label
import enrutadoseia.composeapp.generated.resources.trip_stops_section
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RouteDetailPassengerScreen(
    viewModel: RouteDetailPassengerViewModel,
    onBackClick: () -> Unit,
    onBookingCreated: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailPassengerEvent.NavigateBack -> onBackClick()
            RouteDetailPassengerEvent.BookingCreated -> onBookingCreated()
            RouteDetailPassengerEvent.NavigateToPassengerBookings -> onBookingCreated()
        }
    }

    RouteDetailPassengerContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailPassengerContent(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit
) {
    val confirmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.route_detail_passenger_title),
                onBack = { onAction(RouteDetailPassengerAction.OnBackClick) },
            )
        }
    ) { padding ->
        when {
            state.isLoading -> DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = Spacing.xl)
                ) {
                    state.trip?.let { trip ->
                        item {
                            DriverAndVehicleSection(
                                driver = state.driver,
                                vehicle = state.vehicle,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
                            )
                        }
                        item { HorizontalDivider() }
                        item {
                            TripSummarySection(
                                trip = trip,
                                availableSeats = state.availableSeats,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
                            )
                        }
                        item { HorizontalDivider() }
                        item {
                            StopsSection(
                                trip = trip,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
                            )
                        }
                        if (trip.messageToPassengers.isNotBlank()) {
                            item { HorizontalDivider() }
                            item {
                                DriverMessageSection(
                                    message = trip.messageToPassengers,
                                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
                                )
                            }
                        }
                        item { HorizontalDivider() }
                        item {
                            BookingCtaSection(
                                availableSeats = state.availableSeats,
                                alreadyRequested = state.alreadyRequested,
                                isBooking = state.isBooking,
                                onAction = onAction,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
                            )
                        }
                        state.error?.let { error ->
                            item {
                                Text(
                                    text = stringResource(error.asStringResource()),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = Spacing.lg)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showConfirmSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(RouteDetailPassengerAction.OnDismissConfirmSheet) },
            sheetState = confirmSheetState
        ) {
            ConfirmRequestSheetContent(
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DriverAndVehicleSection(
    driver: PublicProfile?,
    vehicle: Vehicle?,
    modifier: Modifier = Modifier
) {
    val driverName = driver?.name?.takeIf { it.isNotBlank() }
        ?: stringResource(Res.string.trip_driver_placeholder)
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_driver_section),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(name = driverName, photoUrl = driver?.photoUrl, size = 36.dp) // avatar-intrinsic size
            Spacer(modifier = Modifier.size(Spacing.sm))
            Column {
                Text(
                    text = driverName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                vehicle?.let { v ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.directions_car_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp) // icon-intrinsic size
                        )
                        Spacer(modifier = Modifier.size(Spacing.xs))
                        Text(
                            text = "${v.brand} ${v.model} · ${v.color} · ${v.year} · ${v.licensePlate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripSummarySection(
    trip: Trip,
    availableSeats: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = formatEpochShort(trip.departureTime),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${trip.origin.name} → ${trip.destination.name}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
            SeatsBadge(availableSeats = availableSeats)
            val contribText = if ((trip.contributionPerPassenger ?: 0) > 0)
                "$${trip.contributionPerPassenger}"
            else
                stringResource(Res.string.trip_contribution_free)
            Text(
                text = stringResource(Res.string.trip_contribution_label, contribText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeatsBadge(availableSeats: Int, modifier: Modifier = Modifier) {
    val (bg, fg) = if (availableSeats > 0)
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    Surface(shape = MaterialTheme.shapes.extraSmall, color = bg, modifier = modifier) {
        Text(
            text = stringResource(Res.string.available_seats_label, availableSeats),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg,
            // vertical inset is a fine visual tweak below the 4dp scale step, kept as a literal
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 3.dp)
        )
    }
}

@Composable
private fun StopsSection(trip: Trip, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_stops_section),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        StopsTimeline(
            origin = trip.origin,
            waypoints = trip.waypoints,
            destination = trip.destination
        )
    }
}

@Composable
private fun DriverMessageSection(message: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_message_driver_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "\"$message\"",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(Spacing.md)
            )
        }
    }
}

@Composable
private fun BookingCtaSection(
    availableSeats: Int,
    alreadyRequested: Boolean,
    isBooking: Boolean,
    onAction: (RouteDetailPassengerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        when {
            alreadyRequested -> Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.book_request_sent))
            }

            availableSeats <= 0 -> Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.no_seats_available))
            }

            else -> {
                Button(
                    onClick = { onAction(RouteDetailPassengerAction.OnOpenConfirmSheet) },
                    enabled = !isBooking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isBooking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.book_request_button))
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = stringResource(Res.string.book_request_subtext),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ConfirmRequestSheetContent(
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

@Preview
@Composable
private fun RouteDetailPassengerContentPreview() {
    CarpoolTheme {
        RouteDetailPassengerContent(
            state = RouteDetailPassengerUiState(
                isLoading = false,
                trip = Trip(
                    id = "1", routeId = "r1", driverId = "d1", vehicleId = "v1",
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    destination = Place.UNIVERSITY_EIA,
                    waypoints = emptyList(),
                    departureTime = 1746360000000L,
                    status = TripStatus.Active
                ),
                vehicle = Vehicle(
                    id = "v1", driverId = "d1", brand = "Toyota", model = "Corolla",
                    licensePlate = "ABC123", color = "Blanco", year = 2020, seatsAvailable = 3
                ),
                driver = PublicProfile(id = "d1", name = "Juan Pablo"),
                availableSeats = 3
            ),
            onAction = {}
        )
    }
}
