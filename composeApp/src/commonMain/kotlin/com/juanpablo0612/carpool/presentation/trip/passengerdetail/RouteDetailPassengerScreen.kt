package com.juanpablo0612.carpool.presentation.trip.passengerdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.BookingCtaSection
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.ConfirmRequestSheetContent
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.DriverAndVehicleSection
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.DriverMessageSection
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.StopsSection
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.TripSummarySection
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.route_detail_passenger_title
import org.jetbrains.compose.resources.stringResource

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
