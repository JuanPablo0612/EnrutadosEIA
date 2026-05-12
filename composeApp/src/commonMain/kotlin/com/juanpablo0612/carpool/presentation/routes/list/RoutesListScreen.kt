package com.juanpablo0612.carpool.presentation.routes.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.presentation.routes.list.components.RouteCard
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_description
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_title
import enrutadoseia.composeapp.generated.resources.routes_empty_description
import enrutadoseia.composeapp.generated.resources.routes_empty_title
import enrutadoseia.composeapp.generated.resources.routes_list_new_route
import enrutadoseia.composeapp.generated.resources.routes_list_subtitle
import enrutadoseia.composeapp.generated.resources.routes_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RoutesListScreen(
    viewModel: RoutesListViewModel,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToCreateTrip: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RoutesListEvent.NavigateToCreateRoute -> onNavigateToCreateRoute()
            is RoutesListEvent.NavigateToRouteDetail -> onNavigateToRouteDetail(event.routeId)
            is RoutesListEvent.NavigateToCreateTrip -> onNavigateToCreateTrip(event.routeId)
            RoutesListEvent.NavigateBack -> onBackClick()
        }
    }

    RoutesListContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesListContent(
    state: RoutesListUiState,
    onAction: (RoutesListAction) -> Unit
) {
    if (state.pendingDeleteRouteId != null) {
        ConfirmDialog(
            title = stringResource(Res.string.route_delete_confirm_title),
            description = stringResource(Res.string.route_delete_confirm_description),
            confirmText = stringResource(Res.string.route_delete_confirm_button),
            onConfirm = { onAction(RoutesListAction.OnConfirmDelete) },
            onDismiss = { onAction(RoutesListAction.OnDismissDelete) },
            isDestructive = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(Res.string.routes_list_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = stringResource(Res.string.routes_list_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(RoutesListAction.OnBackClick) }) {
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
            ExtendedFloatingActionButton(
                text = { Text(stringResource(Res.string.routes_list_new_route)) },
                icon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = null
                    )
                },
                onClick = { onAction(RoutesListAction.OnCreateRouteClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        when {
            state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            state.routes.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.location_on_24px),
                title = stringResource(Res.string.routes_empty_title),
                description = stringResource(Res.string.routes_empty_description),
                modifier = Modifier.fillMaxSize().padding(padding),
                primaryAction = ActionButton(stringResource(Res.string.routes_list_new_route)) {
                    onAction(RoutesListAction.OnCreateRouteClick)
                }
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(state.routes, key = { it.route.id }) { routeWithStats ->
                        RouteCard(
                            routeWithStats = routeWithStats,
                            onClick = { onAction(RoutesListAction.OnRouteClick(routeWithStats.route.id)) },
                            onPublishTripClick = { onAction(RoutesListAction.OnPublishTripClick(routeWithStats.route.id)) },
                            onEditClick = { onAction(RoutesListAction.OnRouteClick(routeWithStats.route.id)) },
                            onDuplicateClick = { onAction(RoutesListAction.OnDuplicateRouteClick(routeWithStats.route.id)) },
                            onDeleteClick = { onAction(RoutesListAction.OnDeleteRouteClick(routeWithStats.route.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RoutesListEmptyPreview() {
    CarpoolTheme {
        RoutesListContent(
            state = RoutesListUiState(isLoading = false),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun RoutesListWithDataPreview() {
    val origin = Place(id = "1", name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6)
    val destination = Place(id = "2", name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59)
    CarpoolTheme {
        RoutesListContent(
            state = RoutesListUiState(
                isLoading = false,
                routes = listOf(
                    RouteWithStats(
                        route = Route(
                            id = "r1",
                            driverId = "d1",
                            origin = origin,
                            destination = destination,
                            waypoints = emptyList(),
                            name = "Ida a clase"
                        )
                    )
                )
            ),
            onAction = {}
        )
    }
}
