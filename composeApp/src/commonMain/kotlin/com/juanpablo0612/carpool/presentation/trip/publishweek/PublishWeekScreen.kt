package com.juanpablo0612.carpool.presentation.trip.publishweek

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.route.orderedDays
import com.juanpablo0612.carpool.presentation.trip.TripError
import com.juanpablo0612.carpool.presentation.trip.asStringResource
import com.juanpablo0612.carpool.presentation.trip.publish.components.SectionLabel
import com.juanpablo0612.carpool.presentation.trip.publish.components.SingleVehicleCard
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripContributionSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripMessageSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripSeatsSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.VehicleRadioItem
import com.juanpablo0612.carpool.presentation.trip.publishweek.components.WeekSlotRow
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.formatLongDate
import com.juanpablo0612.carpool.presentation.ui.util.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.calendar_month_24px
import enrutadoseia.composeapp.generated.resources.check_24px
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.month_names
import enrutadoseia.composeapp.generated.resources.publish_week_all_published_body
import enrutadoseia.composeapp.generated.resources.publish_week_all_published_title
import enrutadoseia.composeapp.generated.resources.publish_week_edit_route
import enrutadoseia.composeapp.generated.resources.publish_week_no_schedule_body
import enrutadoseia.composeapp.generated.resources.publish_week_no_schedule_title
import enrutadoseia.composeapp.generated.resources.publish_week_publish_button
import enrutadoseia.composeapp.generated.resources.publish_week_settings_title
import enrutadoseia.composeapp.generated.resources.publish_week_title
import enrutadoseia.composeapp.generated.resources.publish_week_window_label
import enrutadoseia.composeapp.generated.resources.route_from_to
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import enrutadoseia.composeapp.generated.resources.trip_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.trip_register_vehicle_action
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Instant

@Composable
fun PublishWeekScreen(
    viewModel: PublishWeekViewModel,
    onBackClick: () -> Unit,
    onTripsPublished: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PublishWeekEvent.TripsPublished -> onTripsPublished()
            PublishWeekEvent.NavigateBack -> onBackClick()
            PublishWeekEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            is PublishWeekEvent.NavigateToRouteDetail -> onNavigateToRouteDetail(event.routeId)
        }
    }

    PublishWeekContent(state = state, onAction = viewModel::onAction)
}

@Composable
fun PublishWeekContent(
    state: PublishWeekUiState,
    onAction: (PublishWeekAction) -> Unit,
) {
    val dayNames = stringArrayResource(Res.array.day_names_short).toList()
    val monthNames = stringArrayResource(Res.array.month_names).toList()
    val connector = stringResource(Res.string.date_of_connector)
    val am = stringResource(Res.string.time_am)
    val pm = stringResource(Res.string.time_pm)
    val dayLabel: (LocalDate) -> String = { formatLongDate(it.year, it.month.number, it.day, dayNames, monthNames, connector) }
    val timeLabel: (LocalTime) -> String = { formatShortTime(it.hour, it.minute, am, pm) }
    val instantTime: (Instant) -> String = { timeLabel(it.toLocalDateTime(TimeZone.currentSystemDefault()).time) }

    val canPublish = !state.isLoading && state.route != null && !state.scheduleMissing && !state.nothingToPublish

    Scaffold(
        contentWindowInsets = ScreenInsets,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.publish_week_title),
                onBack = { onAction(PublishWeekAction.OnBackClick) },
            )
        },
        bottomBar = {
            if (canPublish) {
                Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        PrimaryButton(
                            text = pluralStringResource(Res.plurals.publish_week_publish_button, state.selectedCount, state.selectedCount),
                            onClick = { onAction(PublishWeekAction.OnPublishClick) },
                            isLoading = state.isPublishing,
                            modifier = Modifier
                                .windowInsetsPadding(BottomBarInsets)
                                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                        )
                    }
                }
            }
        },
    ) { padding ->
        val contentModifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
        val route = state.route
        when {
            state.isLoading -> DetailSkeleton(modifier = contentModifier)
            route == null -> ErrorState(
                description = stringResource((state.error ?: TripError.Unknown).asStringResource()),
                onRetry = { onAction(PublishWeekAction.OnRetry) },
                modifier = contentModifier,
            )
            state.scheduleMissing -> EmptyState(
                icon = vectorResource(Res.drawable.calendar_month_24px),
                title = stringResource(Res.string.publish_week_no_schedule_title),
                description = stringResource(Res.string.publish_week_no_schedule_body),
                primaryAction = ActionButton(stringResource(Res.string.publish_week_edit_route)) { onAction(PublishWeekAction.OnEditRouteClick) },
                modifier = contentModifier,
            )
            state.nothingToPublish -> EmptyState(
                icon = vectorResource(Res.drawable.check_24px),
                title = stringResource(Res.string.publish_week_all_published_title),
                description = stringResource(Res.string.publish_week_all_published_body),
                modifier = contentModifier,
            )
            else -> LazyColumn(
                modifier = contentModifier.imePadding(),
                contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                item(key = "header") {
                    val days = orderedDays.filter { it.first in route.recurringDays }.map { stringResource(it.second) }
                    RouteScheduleHeader(
                        name = route.name.ifBlank { stringResource(Res.string.route_from_to, route.origin.name, route.destination.name) },
                        endpoints = stringResource(Res.string.route_from_to, route.origin.name, route.destination.name),
                        schedule = (days + listOfNotNull(route.typicalDepartureTime?.let(timeLabel))).joinToString(" · "),
                        onEditRoute = { onAction(PublishWeekAction.OnEditRouteClick) },
                    )
                }
                item(key = "slots") {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        SectionLabel(text = stringResource(Res.string.publish_week_window_label))
                        CarpoolListCard(contentPadding = PaddingValues(vertical = Spacing.xs)) {
                            state.slots.forEachIndexed { index, slot ->
                                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                WeekSlotRow(
                                    slot = slot,
                                    dayLabel = dayLabel(slot.date),
                                    timeLabel = instantTime(slot.departure),
                                    conflictTimeLabel = (slot.status as? SlotStatus.Conflict)?.departure?.let(instantTime),
                                    isSelected = slot.date in state.selectedDates,
                                    onToggle = { onAction(PublishWeekAction.OnToggleDay(slot.date)) },
                                )
                            }
                        }
                    }
                }
                item(key = "settings") {
                    SectionLabel(text = stringResource(Res.string.publish_week_settings_title))
                }
                item(key = "vehicle") {
                    WeekVehiclePicker(state, onAction)
                }
                if (state.vehicles.isNotEmpty()) {
                    item(key = "seats") {
                        TripSeatsSection(state.seatCount, state.selectedVehicle, { onAction(PublishWeekAction.OnSetSeats(it)) })
                    }
                    item(key = "contribution") {
                        TripContributionSection(state.contributionPerPassenger, { onAction(PublishWeekAction.OnSetContribution(it)) })
                    }
                    item(key = "message") {
                        TripMessageSection(state.message, { onAction(PublishWeekAction.OnSetMessage(it)) })
                    }
                }
                state.error?.let { error ->
                    item(key = "error") {
                        ErrorMessage(
                            message = stringResource(error.asStringResource()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { liveRegion = LiveRegionMode.Polite },
                        )
                    }
                }
            }
        }
    }
}

/** The route being published: its name, its ends, its usual days and time, and a way to edit it. */
@Composable
private fun RouteScheduleHeader(name: String, endpoints: String, schedule: String, onEditRoute: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(text = name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        Text(text = endpoints, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = schedule,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onEditRoute) { Text(stringResource(Res.string.publish_week_edit_route)) }
        }
    }
}

@Composable
private fun WeekVehiclePicker(state: PublishWeekUiState, onAction: (PublishWeekAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.select_vehicle_section),
            style = MaterialTheme.typography.titleSmall,
        )
        when {
            state.vehicles.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.directions_car_24px),
                title = stringResource(Res.string.trip_no_vehicle_title),
                description = "",
                primaryAction = ActionButton(stringResource(Res.string.trip_register_vehicle_action)) {
                    onAction(PublishWeekAction.OnRegisterVehicleClick)
                },
            )
            state.vehicles.size == 1 -> SingleVehicleCard(vehicle = state.vehicles.first(), onChangeClick = null)
            else -> Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                state.vehicles.forEach { vehicle ->
                    VehicleRadioItem(
                        vehicle = vehicle,
                        isSelected = vehicle.id == state.selectedVehicleId,
                        onClick = { onAction(PublishWeekAction.OnVehicleSelected(vehicle.id)) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PublishWeekPreview() {
    val zone = TimeZone.of("America/Bogota")
    fun at(day: Int): Instant = LocalDateTime(2026, 9, day, 6, 45).toInstant(zone)
    CarpoolTheme {
        PublishWeekContent(
            state = PublishWeekUiState(
                isLoading = false,
                route = Route(
                    id = "r1",
                    driverId = "d1",
                    origin = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58),
                    destination = Place.EIA_LAS_PALMAS,
                    waypoints = emptyList(),
                    name = "Ida a la U",
                    recurringDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                    typicalDepartureTime = LocalTime(6, 45),
                ),
                slots = listOf(
                    RecurringTripSlot(LocalDate(2026, 9, 28), at(28), SlotStatus.Available),
                    RecurringTripSlot(LocalDate(2026, 9, 30), at(30), SlotStatus.AlreadyPublished("t1")),
                ),
                selectedDates = setOf(LocalDate(2026, 9, 28)),
                vehicles = listOf(
                    Vehicle(id = "v1", driverId = "d1", brand = "Mazda", model = "3", licensePlate = "ABC123", color = "Gris", year = 2020, seatsAvailable = 4)
                ),
                selectedVehicleId = "v1",
                seatCount = 3,
            ),
            onAction = {},
        )
    }
}
