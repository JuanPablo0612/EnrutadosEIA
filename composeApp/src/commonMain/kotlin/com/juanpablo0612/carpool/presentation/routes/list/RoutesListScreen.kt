package com.juanpablo0612.carpool.presentation.routes.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.presentation.routes.list.components.RouteCard
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.routes_empty_subtitle
import enrutadoseia.composeapp.generated.resources.routes_empty_title
import enrutadoseia.composeapp.generated.resources.routes_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RoutesListScreen(
    viewModel: RoutesListViewModel,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRouteDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RoutesListEvent.NavigateToCreateRoute -> onNavigateToCreateRoute()
            is RoutesListEvent.NavigateToRouteDetail -> onNavigateToRouteDetail(event.routeId)
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.routes_list_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(RoutesListAction.OnCreateRouteClick) },
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
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.routes.isEmpty() -> {
                EmptyRoutesState(
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.routes, key = { it.id }) { route ->
                        RouteCard(
                            route = route,
                            onClick = { onAction(RoutesListAction.OnRouteClick(route.id)) }
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
                    Route(
                        id = "r1",
                        driverId = "d1",
                        origin = origin,
                        destination = destination,
                        waypoints = emptyList(),
                        targetTime = LocalTime(7, 30),
                        daysOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                        type = RouteType.ToUniversity
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Composable
private fun EmptyRoutesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.location_on_24px),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.routes_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.routes_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}
