package com.juanpablo0612.carpool.presentation.routes.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.routes.search.components.AvailableRouteCard
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.logout_24px
import enrutadoseia.composeapp.generated.resources.logout_title
import enrutadoseia.composeapp.generated.resources.no_results_found
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_places
import enrutadoseia.composeapp.generated.resources.switch_role
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoutesScreen(
    viewModel: SearchRoutesViewModel,
    user: User,
    isDualRole: Boolean,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit,
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
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoutesContent(
    state: SearchRoutesUiState,
    user: User,
    isDualRole: Boolean,
    onAction: (SearchRoutesAction) -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(Res.string.passenger_home_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = user.name ?: user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (isDualRole) {
                        IconButton(onClick = onSwitchRole) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                                contentDescription = stringResource(Res.string.switch_role)
                            )
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.logout_24px),
                            contentDescription = stringResource(Res.string.logout_title)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.trips.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_results_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
            onLogout = {}
        )
    }
}
