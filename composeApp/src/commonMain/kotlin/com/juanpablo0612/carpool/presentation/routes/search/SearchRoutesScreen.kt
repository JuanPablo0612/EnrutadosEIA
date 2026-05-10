package com.juanpablo0612.carpool.presentation.routes.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.routes.search.components.AvailableRouteCard
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_empty_subtitle
import enrutadoseia.composeapp.generated.resources.search_empty_title
import enrutadoseia.composeapp.generated.resources.search_places
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SearchRoutesScreen(
    viewModel: SearchRoutesViewModel,
    user: User,
    isDualRole: Boolean,
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchRoutesEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
        }
    }

    SearchRoutesContent(
        state = state,
        user = user,
        isDualRole = isDualRole,
        onAction = viewModel::onAction,
        onSwitchRole = onSwitchRole,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun SearchRoutesContent(
    state: SearchRoutesUiState,
    user: User,
    isDualRole: Boolean,
    onAction: (SearchRoutesAction) -> Unit,
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolTopBar(
                title = stringResource(Res.string.passenger_home_title),
                user = user,
                isDualRole = isDualRole,
                currentRoleLabel = stringResource(Res.string.role_selector_passenger_title),
                onAvatarClick = onNavigateToProfile,
                onRoleToggle = if (isDualRole) onSwitchRole else null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            OutlinedTextField(
                value = state.query,
                onValueChange = { onAction(SearchRoutesAction.OnQueryChanged(it)) },
                placeholder = { Text(stringResource(Res.string.search_places)) },
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.search_24px),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())

                state.trips.isEmpty() -> EmptyState(
                    icon = vectorResource(Res.drawable.search_24px),
                    title = stringResource(Res.string.search_empty_title),
                    description = stringResource(Res.string.search_empty_subtitle),
                    modifier = Modifier.fillMaxSize()
                )

                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(bottom = Spacing.lg)
                ) {
                    items(state.trips, key = { it.id }) { trip ->
                        AvailableRouteCard(
                            trip = trip,
                            onClick = { onAction(SearchRoutesAction.OnTripClick(trip.id)) }
                        )
                    }
                }
            }
        }
    }
}

private val previewUser = User(
    id = "1",
    email = "pasajero@eia.edu.co",
    name = "Maria García",
    isEmailVerified = true,
    isPassenger = true,
    isDriver = false
)

@Preview
@Composable
private fun SearchRoutesEmptyPreview() {
    CarpoolTheme {
        SearchRoutesContent(
            state = SearchRoutesUiState(isLoading = false),
            user = previewUser,
            isDualRole = false,
            onAction = {},
            onSwitchRole = {},
            onNavigateToProfile = {}
        )
    }
}
