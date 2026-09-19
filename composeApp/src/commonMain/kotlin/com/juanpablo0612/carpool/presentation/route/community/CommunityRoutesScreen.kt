package com.juanpablo0612.carpool.presentation.route.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.route.community.components.CommunityRouteCard
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.community_routes_empty_description
import enrutadoseia.composeapp.generated.resources.community_routes_empty_title
import enrutadoseia.composeapp.generated.resources.community_routes_subtitle
import enrutadoseia.composeapp.generated.resources.community_routes_title
import enrutadoseia.composeapp.generated.resources.location_on_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CommunityRoutesScreen(
    viewModel: CommunityRoutesViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CommunityRoutesEvent.NavigateBack -> onBack()
        }
    }

    CommunityRoutesContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CommunityRoutesContent(
    state: CommunityRoutesUiState,
    onAction: (CommunityRoutesAction) -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.community_routes_title),
                subtitle = stringResource(Res.string.community_routes_subtitle),
                onBack = { onAction(CommunityRoutesAction.OnBackClick) },
            )
        }
    ) { padding ->
        when {
            state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            state.error != null -> ErrorState(
                description = stringResource(state.error.asStringResource()),
                onRetry = { onAction(CommunityRoutesAction.OnRetry) },
                modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.lg)
            )
            state.routes.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.location_on_24px),
                title = stringResource(Res.string.community_routes_empty_title),
                description = stringResource(Res.string.community_routes_empty_description),
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    state.actionError?.let { actionError ->
                        item(key = "action_error") {
                            ErrorMessage(
                                message = stringResource(actionError.asStringResource()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAction(CommunityRoutesAction.OnDismissActionError) }
                            )
                        }
                    }
                    items(state.routes, key = { it.id }) { route ->
                        CommunityRouteCard(
                            route = route,
                            onReuseClick = { onAction(CommunityRoutesAction.OnReuseClick(route.id)) },
                            isReusing = state.reusingRouteId == route.id
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CommunityRoutesEmptyPreview() {
    CarpoolTheme {
        CommunityRoutesContent(
            state = CommunityRoutesUiState(isLoading = false),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun CommunityRoutesWithDataPreview() {
    val origin = Place(id = "1", name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6)
    val destination = Place(id = "2", name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59)
    CarpoolTheme {
        CommunityRoutesContent(
            state = CommunityRoutesUiState(
                isLoading = false,
                routes = listOf(
                    Route(
                        id = "r1",
                        driverId = "d1",
                        origin = origin,
                        destination = destination,
                        waypoints = emptyList(),
                        name = "Ida a clase",
                        isShared = true
                    )
                )
            ),
            onAction = {}
        )
    }
}
