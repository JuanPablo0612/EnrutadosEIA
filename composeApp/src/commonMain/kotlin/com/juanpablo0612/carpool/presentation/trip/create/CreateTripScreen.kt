package com.juanpablo0612.carpool.presentation.trip.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.trip.asStringResource
import com.juanpablo0612.carpool.presentation.trip.create.components.DateChip
import com.juanpablo0612.carpool.presentation.trip.create.components.RouteSummaryCard
import com.juanpablo0612.carpool.presentation.trip.create.components.SectionLabel
import com.juanpablo0612.carpool.presentation.trip.create.components.SingleVehicleCard
import com.juanpablo0612.carpool.presentation.trip.create.components.TripContributionSection
import com.juanpablo0612.carpool.presentation.trip.create.components.TripMessageSection
import com.juanpablo0612.carpool.presentation.trip.create.components.TripSeatsSection
import com.juanpablo0612.carpool.presentation.trip.create.components.TripTimeSection
import com.juanpablo0612.carpool.presentation.trip.create.components.TripWhenSection
import com.juanpablo0612.carpool.presentation.trip.create.components.VehicleRadioItem
import com.juanpablo0612.carpool.presentation.trip.create.components.formatPesos
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatLongDate
import com.juanpablo0612.carpool.presentation.ui.util.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel
import enrutadoseia.composeapp.generated.resources.confirm
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.month_names
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import enrutadoseia.composeapp.generated.resources.create_trip_title
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.publish_trip
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary_with_contribution
import enrutadoseia.composeapp.generated.resources.trip_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.trip_register_vehicle_action
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CreateTripScreen(
    viewModel: CreateTripViewModel,
    onBackClick: () -> Unit,
    onTripPublished: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToVehiclesList: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateTripEvent.TripPublished -> onTripPublished()
            CreateTripEvent.NavigateBack -> onBackClick()
            CreateTripEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            CreateTripEvent.NavigateToVehiclesList -> onNavigateToVehiclesList()
        }
    }

    CreateTripContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripContent(
    state: CreateTripUiState,
    onAction: (CreateTripAction) -> Unit
) {
    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.departureDate.let {
                kotlinx.datetime.LocalDateTime(it, LocalTime(0, 0))
                    .toInstant(TimeZone.UTC).toEpochMilliseconds()
            }
        )
        DatePickerDialog(
            onDismissRequest = { onAction(CreateTripAction.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { ms ->
                        val date = Instant.fromEpochMilliseconds(ms)
                            .toLocalDateTime(TimeZone.UTC).date
                        onAction(CreateTripAction.OnDateSelected(date))
                    }
                }) { Text(stringResource(Res.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(CreateTripAction.OnDismissDatePicker) }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.departureTime.hour,
            initialMinute = state.departureTime.minute,
            is24Hour = false
        )
        BasicAlertDialog(onDismissRequest = { onAction(CreateTripAction.OnDismissTimePicker) }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier.padding(Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onAction(CreateTripAction.OnDismissTimePicker) }) {
                            Text(stringResource(Res.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        TextButton(onClick = {
                            onAction(
                                CreateTripAction.OnTimeSelected(
                                    LocalTime(timePickerState.hour, timePickerState.minute)
                                )
                            )
                        }) {
                            Text(stringResource(Res.string.confirm))
                        }
                    }
                }
            }
        }
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val tomorrow = today.plus(1, DateTimeUnit.DAY)

    val dateChipState = when (state.departureDate) {
        today -> DateChip.Today
        tomorrow -> DateChip.Tomorrow
        else -> DateChip.Other
    }

    val dayNamesShort = stringArrayResource(Res.array.day_names_short)
    val monthNames = stringArrayResource(Res.array.month_names)
    val dateConnector = stringResource(Res.string.date_of_connector)
    val amMarker = stringResource(Res.string.time_am)
    val pmMarker = stringResource(Res.string.time_pm)
    val formattedDate = formatLongDate(
        state.departureDate.year,
        state.departureDate.monthNumber,
        state.departureDate.dayOfMonth,
        dayNamesShort.toList(),
        monthNames.toList(),
        dateConnector
    )
    val formattedTime = formatShortTime(state.departureTime.hour, state.departureTime.minute, amMarker, pmMarker)

    val contributionText = state.contributionPerPassenger?.let {
        formatPesos(it)
    }
    val summaryText = if (contributionText != null) {
        stringResource(
            Res.string.trip_bottom_summary_with_contribution,
            formattedDate, formattedTime, state.seatCount, contributionText
        )
    } else {
        stringResource(
            Res.string.trip_bottom_summary,
            formattedDate, formattedTime, state.seatCount
        )
    }

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.create_trip_title),
                onBack = { onAction(CreateTripAction.OnBackClick) },
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                Surface(
                    tonalElevation = Elevation.raised,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Text(
                            text = summaryText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Button(
                            onClick = { onAction(CreateTripAction.OnPublishClick) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.canPublish && !state.isPublishing
                        ) {
                            if (state.isPublishing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp), // component-intrinsic spinner size
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp // hairline stroke
                                )
                            } else {
                                Text(stringResource(Res.string.publish_trip))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(bottom = Spacing.lg)
        ) {
            // Route summary card
            state.route?.let { route ->
                item {
                    RouteSummaryCard(
                        originName = route.origin.name,
                        destinationName = route.destination.name,
                        waypointCount = route.waypoints.size,
                        modifier = Modifier.padding(Spacing.lg)
                    )
                }
            }

            // When section
            item {
                TripWhenSection(
                    dateChipState = dateChipState,
                    formattedDate = formattedDate,
                    onSelectToday = { onAction(CreateTripAction.OnSelectTodayDate) },
                    onSelectTomorrow = { onAction(CreateTripAction.OnSelectTomorrowDate) },
                    onShowDatePicker = { onAction(CreateTripAction.OnShowDatePicker) },
                )
            }

            // Time section
            item {
                TripTimeSection(
                    formattedTime = formattedTime,
                    onShowTimePicker = { onAction(CreateTripAction.OnShowTimePicker) },
                )
            }

            // Vehicle section
            item {
                SectionLabel(
                    text = stringResource(Res.string.select_vehicle_section),
                    modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
                )
            }

            when {
                state.vehicles.isEmpty() -> item {
                    EmptyState(
                        icon = vectorResource(Res.drawable.directions_car_24px),
                        title = stringResource(Res.string.trip_no_vehicle_title),
                        description = "",
                        primaryAction = ActionButton(
                            label = stringResource(Res.string.trip_register_vehicle_action),
                            onClick = { onAction(CreateTripAction.OnNavigateToRegisterVehicle) }
                        ),
                        modifier = Modifier.padding(Spacing.lg)
                    )
                }
                state.vehicles.size == 1 -> item {
                    SingleVehicleCard(
                        vehicle = state.vehicles.first(),
                        onChangeClick = { onAction(CreateTripAction.OnNavigateToVehiclesList) },
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }
                else -> item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(state.vehicles, key = { it.id }) { vehicle ->
                            VehicleRadioItem(
                                vehicle = vehicle,
                                isSelected = vehicle.id == state.selectedVehicleId,
                                onClick = { onAction(CreateTripAction.OnVehicleSelected(vehicle.id)) }
                            )
                        }
                    }
                }
            }

            // Seat count stepper
            if (state.vehicles.isNotEmpty()) {
                item {
                    TripSeatsSection(
                        seatCount = state.seatCount,
                        selectedVehicle = state.selectedVehicle,
                        onChange = { onAction(CreateTripAction.OnSetSeats(it)) },
                    )
                }

                // Contribution field
                item {
                    TripContributionSection(
                        contributionPerPassenger = state.contributionPerPassenger,
                        onContributionChange = { onAction(CreateTripAction.OnSetContribution(it)) },
                    )
                }

                // Message field
                item {
                    TripMessageSection(
                        message = state.messageToPassengers,
                        onMessageChange = { onAction(CreateTripAction.OnSetMessage(it)) },
                    )
                }
            }

            // Error
            state.error?.let { error ->
                item {
                    Text(
                        text = stringResource(error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateTripContentPreview() {
    CarpoolTheme {
        CreateTripContent(
            state = CreateTripUiState(
                isLoading = false,
                route = Route(
                    id = "r1", driverId = "d1",
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    destination = Place.UNIVERSITY_EIA,
                    waypoints = emptyList()
                ),
                vehicles = listOf(
                    Vehicle(
                        id = "v1", driverId = "d1", brand = "Toyota", model = "Corolla",
                        licensePlate = "ABC123", color = "Blanco", year = 2020, seatsAvailable = 3
                    )
                ),
                selectedVehicleId = "v1",
                seatCount = 3,
                departureTime = LocalTime(7, 0)
            ),
            onAction = {}
        )
    }
}
