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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.utils.formatLongDate
import com.juanpablo0612.carpool.presentation.utils.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.cancel
import enrutadoseia.composeapp.generated.resources.confirm
import enrutadoseia.composeapp.generated.resources.create_trip_title
import enrutadoseia.composeapp.generated.resources.date_other
import enrutadoseia.composeapp.generated.resources.date_today
import enrutadoseia.composeapp.generated.resources.date_tomorrow
import enrutadoseia.composeapp.generated.resources.departure_time_section_label
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.publish_trip
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary_with_contribution
import enrutadoseia.composeapp.generated.resources.trip_change_vehicle
import enrutadoseia.composeapp.generated.resources.trip_contribution_hint
import enrutadoseia.composeapp.generated.resources.trip_contribution_section
import enrutadoseia.composeapp.generated.resources.trip_message_counter
import enrutadoseia.composeapp.generated.resources.trip_message_placeholder
import enrutadoseia.composeapp.generated.resources.trip_message_section
import enrutadoseia.composeapp.generated.resources.trip_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.trip_register_vehicle_action
import enrutadoseia.composeapp.generated.resources.trip_seats_helper
import enrutadoseia.composeapp.generated.resources.trip_seats_section
import enrutadoseia.composeapp.generated.resources.trip_when_section
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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
            Surface(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onAction(CreateTripAction.OnDismissTimePicker) }) {
                            Text(stringResource(Res.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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

    val formattedDate = formatLongDate(
        state.departureDate.year,
        state.departureDate.monthNumber,
        state.departureDate.dayOfMonth
    )
    val formattedTime = formatShortTime(state.departureTime.hour, state.departureTime.minute)

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
            TopAppBar(
                title = { Text(stringResource(Res.string.create_trip_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(CreateTripAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                Surface(
                    tonalElevation = 3.dp,
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
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
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
            modifier = Modifier.fillMaxSize().padding(padding),
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
                SectionLabel(
                    text = stringResource(Res.string.trip_when_section),
                    modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.md, bottom = Spacing.sm)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    FilterChip(
                        selected = dateChipState == DateChip.Today,
                        onClick = { onAction(CreateTripAction.OnSelectTodayDate) },
                        label = { Text(stringResource(Res.string.date_today)) }
                    )
                    FilterChip(
                        selected = dateChipState == DateChip.Tomorrow,
                        onClick = { onAction(CreateTripAction.OnSelectTomorrowDate) },
                        label = { Text(stringResource(Res.string.date_tomorrow)) }
                    )
                    FilterChip(
                        selected = dateChipState == DateChip.Other,
                        onClick = { onAction(CreateTripAction.OnShowDatePicker) },
                        label = { Text(stringResource(Res.string.date_other)) }
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Spacing.lg)
                )
            }

            // Time section
            item {
                SectionLabel(
                    text = stringResource(Res.string.departure_time_section_label),
                    modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
                )
                OutlinedButton(
                    onClick = { onAction(CreateTripAction.OnShowTimePicker) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                ) {
                    Text(text = formattedTime)
                }
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
                    SectionLabel(
                        text = stringResource(Res.string.trip_seats_section),
                        modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
                    )
                    NumberStepper(
                        value = state.seatCount,
                        onChange = { onAction(CreateTripAction.OnSetSeats(it)) },
                        min = 1,
                        max = state.selectedVehicle?.seatsAvailable ?: state.seatCount,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                    state.selectedVehicle?.let { v ->
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = stringResource(
                                Res.string.trip_seats_helper,
                                "${v.brand} ${v.model}",
                                v.seatsAvailable
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = Spacing.lg)
                        )
                    }
                }

                // Contribution field
                item {
                    SectionLabel(
                        text = stringResource(Res.string.trip_contribution_section),
                        modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
                    )
                    OutlinedTextField(
                        value = state.contributionPerPassenger?.toString() ?: "",
                        onValueChange = { raw ->
                            val digits = raw.filter { it.isDigit() }
                            onAction(CreateTripAction.OnSetContribution(digits.toIntOrNull()))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg),
                        prefix = { Text("$") },
                        visualTransformation = PesosVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.trip_contribution_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                // Message field
                item {
                    SectionLabel(
                        text = stringResource(Res.string.trip_message_section),
                        modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
                    )
                    OutlinedTextField(
                        value = state.messageToPassengers,
                        onValueChange = { onAction(CreateTripAction.OnSetMessage(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg),
                        placeholder = { Text(stringResource(Res.string.trip_message_placeholder)) },
                        maxLines = 4,
                        minLines = 3
                    )
                    Text(
                        text = stringResource(
                            Res.string.trip_message_counter,
                            state.messageToPassengers.length
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.messageToPassengers.length >= 140)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
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

private enum class DateChip { Today, Tomorrow, Other }

@Composable
private fun RouteSummaryCard(
    originName: String,
    destinationName: String,
    waypointCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = originName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Spacing.sm).size(20.dp)
                )
                Text(
                    text = destinationName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
            }
            if (waypointCount > 0) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "· $waypointCount parada${if (waypointCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SingleVehicleCard(
    vehicle: Vehicle,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${vehicle.color} · ${vehicle.licensePlate} · ${vehicle.seatsAvailable} cupos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onChangeClick) {
                Text(stringResource(Res.string.trip_change_vehicle))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun VehicleRadioItem(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${vehicle.color} · ${vehicle.licensePlate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private class PesosVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val original = text.text
        val formatted = formatPesos(original.toIntOrNull() ?: 0)
            .takeIf { original.isNotEmpty() } ?: ""

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = original.length
        }
        return TransformedText(
            androidx.compose.ui.text.AnnotatedString(formatted),
            offsetMapping
        )
    }
}

private fun formatPesos(amount: Int): String {
    return amount.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
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
