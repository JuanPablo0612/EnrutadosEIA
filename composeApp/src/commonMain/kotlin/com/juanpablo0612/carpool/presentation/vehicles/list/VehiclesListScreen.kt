package com.juanpablo0612.carpool.presentation.vehicles.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.vehicles.list.components.VehicleCard
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.register_vehicle_title
import enrutadoseia.composeapp.generated.resources.vehicles_empty_subtitle
import enrutadoseia.composeapp.generated.resources.vehicles_empty_title
import enrutadoseia.composeapp.generated.resources.vehicles_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun VehiclesListScreen(
    viewModel: VehiclesListViewModel,
    onNavigateToRegisterVehicle: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            VehiclesListEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            VehiclesListEvent.NavigateBack -> onBackClick()
        }
    }

    VehiclesListContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesListContent(
    state: VehiclesListUiState,
    onAction: (VehiclesListAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.vehicles_list_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(VehiclesListAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(VehiclesListAction.OnRegisterVehicleClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.add_24px),
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            state.vehicles.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.directions_car_24px),
                title = stringResource(Res.string.vehicles_empty_title),
                description = stringResource(Res.string.vehicles_empty_subtitle),
                modifier = Modifier.fillMaxSize().padding(padding),
                primaryAction = ActionButton(stringResource(Res.string.register_vehicle_title)) {
                    onAction(VehiclesListAction.OnRegisterVehicleClick)
                }
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(state.vehicles, key = { it.id }) { vehicle ->
                        VehicleCard(vehicle = vehicle)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun VehiclesListEmptyPreview() {
    CarpoolTheme {
        VehiclesListContent(
            state = VehiclesListUiState(isLoading = false),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun VehiclesListWithDataPreview() {
    CarpoolTheme {
        VehiclesListContent(
            state = VehiclesListUiState(
                isLoading = false,
                vehicles = listOf(
                    Vehicle(
                        id = "1",
                        driverId = "driver1",
                        brand = "Toyota",
                        model = "Corolla",
                        licensePlate = "ABC-123",
                        color = "Blanco",
                        year = 2020,
                        seatsAvailable = 3
                    ),
                    Vehicle(
                        id = "2",
                        driverId = "driver1",
                        brand = "Mazda",
                        model = "3",
                        licensePlate = "XYZ-456",
                        color = "Gris",
                        year = 2019,
                        seatsAvailable = 2
                    )
                )
            ),
            onAction = {}
        )
    }
}

