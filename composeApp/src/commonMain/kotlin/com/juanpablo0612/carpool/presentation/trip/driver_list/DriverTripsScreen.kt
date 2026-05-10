package com.juanpablo0612.carpool.presentation.trip.driver_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.departure_time_label
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.my_trips_empty
import enrutadoseia.composeapp.generated.resources.my_trips_empty_subtitle
import enrutadoseia.composeapp.generated.resources.nav_my_trips
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun DriverTripsScreen(
    viewModel: DriverTripsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            DriverTripsEvent.NavigateBack -> onBackClick()
        }
    }

    DriverTripsContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverTripsContent(
    state: DriverTripsUiState,
    onAction: (DriverTripsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.nav_my_trips)) }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> ListSkeleton(
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            state.trips.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.directions_car_24px),
                title = stringResource(Res.string.my_trips_empty),
                description = stringResource(Res.string.my_trips_empty_subtitle),
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(state.trips, key = { it.id }) { trip ->
                    DriverTripCard(trip = trip)
                }
            }
        }
    }
}

@Composable
private fun DriverTripCard(trip: Trip, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.departure_time_label, formatDepartureTime(trip.departureTime)),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                TripStatusBadge(status = trip.status)
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.origin.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = trip.origin.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = Spacing.sm)
                        .size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.destination.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = trip.destination.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun formatDepartureTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    return "$hour:$minute - $day/$month/${local.year}"
}

@Preview
@Composable
private fun DriverTripsEmptyPreview() {
    CarpoolTheme {
        DriverTripsContent(
            state = DriverTripsUiState(isLoading = false, trips = emptyList()),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun DriverTripsWithDataPreview() {
    val origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6)
    CarpoolTheme {
        DriverTripsContent(
            state = DriverTripsUiState(
                isLoading = false,
                trips = listOf(
                    Trip(
                        id = "1", routeId = "r1", driverId = "d1", vehicleId = "v1",
                        origin = origin,
                        destination = Place.UNIVERSITY_EIA,
                        waypoints = emptyList(),
                        departureTime = 1746360000000L,
                        status = TripStatus.Active
                    ),
                    Trip(
                        id = "2", routeId = "r1", driverId = "d1", vehicleId = "v1",
                        origin = origin,
                        destination = Place.UNIVERSITY_EIA,
                        waypoints = emptyList(),
                        departureTime = 1746446400000L,
                        status = TripStatus.Completed
                    )
                )
            ),
            onAction = {}
        )
    }
}
