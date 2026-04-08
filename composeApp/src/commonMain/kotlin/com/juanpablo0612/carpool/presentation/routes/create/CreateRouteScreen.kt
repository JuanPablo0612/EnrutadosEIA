package com.juanpablo0612.carpool.presentation.routes.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.presentation.routes.create.components.DaySelector
import com.juanpablo0612.carpool.presentation.routes.create.components.RouteTypeToggle
import com.juanpablo0612.carpool.presentation.routes.create.components.WaypointItem
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CreateRouteScreen(
    viewModel: CreateRouteViewModel,
    onBackClick: () -> Unit,
    onRouteCreated: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateRouteEvent.NavigateBack -> onBackClick()
            is CreateRouteEvent.RouteCreated -> onRouteCreated()
            is CreateRouteEvent.ShowError -> {
                // Error handling via state is preferred as per AGENTS.md
            }
        }
    }

    CreateRouteContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteContent(
    state: CreateRouteUiState,
    onAction: (CreateRouteAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.create_route_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(CreateRouteAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                RouteTypeToggle(
                    selectedType = state.routeType,
                    onTypeChange = { onAction(CreateRouteAction.OnRouteTypeChange(it)) }
                )
            }

            item {
                SectionHeader(stringResource(Res.string.waypoints_section_title))
            }

            item {
                LocationField(
                    label = if (state.routeType is RouteType.ToUniversity) 
                        stringResource(Res.string.origin_label) 
                    else 
                        stringResource(Res.string.origin_eia_label),
                    place = state.origin,
                    isLocked = state.routeType is RouteType.FromUniversity
                )
            }

            itemsIndexed(state.waypoints) { index, waypoint ->
                WaypointItem(
                    place = waypoint,
                    onRemove = { onAction(CreateRouteAction.OnRemoveWaypoint(index)) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                TextButton(
                    onClick = { 
                        // In a real app, this would open a place picker
                        onAction(CreateRouteAction.OnAddWaypoint(Place(name = "Nueva Parada", address = "...", latitude = 0.0, longitude = 0.0)))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(vectorResource(Res.drawable.add_24px), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.add_waypoint_button))
                }
            }

            item {
                LocationField(
                    label = if (state.routeType is RouteType.FromUniversity) 
                        stringResource(Res.string.destination_label) 
                    else 
                        stringResource(Res.string.destination_eia_label),
                    place = state.destination,
                    isLocked = state.routeType is RouteType.ToUniversity
                )
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
            }

            item {
                val timeLabel = if (state.routeType is RouteType.ToUniversity) 
                    stringResource(Res.string.arrival_time_label) 
                else 
                    stringResource(Res.string.departure_time_label)
                
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(timeLabel, style = MaterialTheme.typography.titleMedium)
                    Text(
                        state.targetTime.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                DaySelector(
                    selectedDays = state.selectedDays,
                    onDayToggled = { onAction(CreateRouteAction.OnDayToggled(it)) },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = { onAction(CreateRouteAction.OnSaveClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.save_route_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun LocationField(
    label: String,
    place: Place?,
    isLocked: Boolean
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = if (isLocked) CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                 else CardDefaults.outlinedCardColors()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                place?.name ?: stringResource(Res.string.select_location_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = if (place == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
