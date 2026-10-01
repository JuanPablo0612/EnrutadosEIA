package com.juanpablo0612.carpool.presentation.vehicle.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.FullLineSpan
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import com.juanpablo0612.carpool.presentation.vehicle.list.components.VehicleCard
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.vehicle_delete_blocked_description
import enrutadoseia.composeapp.generated.resources.vehicle_delete_blocked_title
import enrutadoseia.composeapp.generated.resources.vehicle_delete_confirm_description
import enrutadoseia.composeapp.generated.resources.vehicle_delete_confirm_title
import enrutadoseia.composeapp.generated.resources.vehicle_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.vehicle_delete_blocked_ok
import enrutadoseia.composeapp.generated.resources.vehicle_delete_blocked_view_trip
import enrutadoseia.composeapp.generated.resources.vehicles_add_fab
import enrutadoseia.composeapp.generated.resources.vehicles_empty_subtitle
import enrutadoseia.composeapp.generated.resources.vehicles_empty_title
import enrutadoseia.composeapp.generated.resources.vehicles_list_subtitle
import enrutadoseia.composeapp.generated.resources.vehicles_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun VehiclesListScreen(
    viewModel: VehiclesListViewModel,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToEditVehicle: (String) -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            VehiclesListEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            is VehiclesListEvent.NavigateToEditVehicle -> onNavigateToEditVehicle(event.vehicleId)
            is VehiclesListEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
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
    if (state.vehicleToDelete != null) {
        ConfirmDialog(
            title = stringResource(Res.string.vehicle_delete_confirm_title),
            description = stringResource(Res.string.vehicle_delete_confirm_description),
            confirmText = stringResource(Res.string.vehicle_delete_confirm_button),
            onConfirm = { onAction(VehiclesListAction.OnConfirmDelete) },
            onDismiss = { onAction(VehiclesListAction.OnDismissDeleteDialog) },
            isDestructive = true
        )
    }

    if (state.deleteBlockedVehicle != null) {
        // The blocking trip's id is known (see VehiclesListViewModel.OnDeleteRequest), so this
        // doubles as a real "go fix it" action instead of a dead end — dismiss stays as a plain
        // acknowledgement.
        ConfirmDialog(
            title = stringResource(Res.string.vehicle_delete_blocked_title),
            description = stringResource(Res.string.vehicle_delete_blocked_description),
            confirmText = stringResource(Res.string.vehicle_delete_blocked_view_trip),
            dismissText = stringResource(Res.string.vehicle_delete_blocked_ok),
            onConfirm = { onAction(VehiclesListAction.OnViewBlockingTrip) },
            onDismiss = { onAction(VehiclesListAction.OnDismissBlockedDialog) },
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.vehicles_list_title),
                subtitle = stringResource(Res.string.vehicles_list_subtitle),
                onBack = { onAction(VehiclesListAction.OnBackClick) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAction(VehiclesListAction.OnAddVehicle) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = null
                    )
                },
                text = { Text(stringResource(Res.string.vehicles_add_fab)) }
            )
        }
    ) { padding ->
        val pullRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(VehiclesListAction.Refresh) },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            state.actionError?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                        .clickable { onAction(VehiclesListAction.OnDismissActionError) }
                )
            }
            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                state.vehicles.isEmpty() -> EmptyState(
                    icon = vectorResource(Res.drawable.directions_car_24px),
                    title = stringResource(Res.string.vehicles_empty_title),
                    description = stringResource(Res.string.vehicles_empty_subtitle),
                    modifier = Modifier.fillMaxSize(),
                    primaryAction = ActionButton(stringResource(Res.string.vehicles_add_fab)) {
                        onAction(VehiclesListAction.OnAddVehicle)
                    }
                )
                else -> {
                    CenteredContent(ContentWidth.list, modifier = Modifier.fillMaxSize(), gutter = 0.dp) { margin ->
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(ContentWidth.gridCell),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Spacing.lg).plusHorizontal(margin),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        ) {
                            items(state.vehicles, key = { it.id }) { vehicle ->
                                VehicleCard(
                                    vehicle = vehicle,
                                    totalVehicleCount = state.vehicles.size,
                                    onEdit = { onAction(VehiclesListAction.OnEditVehicle(vehicle.id)) },
                                    onSetPrimary = { onAction(VehiclesListAction.OnSetPrimary(vehicle.id)) },
                                    onDelete = { onAction(VehiclesListAction.OnDeleteRequest(vehicle)) },
                                    // Same destination as the "Edit" menu item.
                                    onClick = { onAction(VehiclesListAction.OnEditVehicle(vehicle.id)) }
                                )
                            }
                        }
                    }
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
                        licensePlate = "ABC123",
                        color = "Blanco",
                        year = 2020,
                        seatsAvailable = 3,
                        isPrimary = true
                    ),
                    Vehicle(
                        id = "2",
                        driverId = "driver1",
                        brand = "Mazda",
                        model = "3",
                        licensePlate = "XYZ456",
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
