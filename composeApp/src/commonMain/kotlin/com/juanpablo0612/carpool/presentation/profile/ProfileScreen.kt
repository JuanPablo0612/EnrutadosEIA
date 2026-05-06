package com.juanpablo0612.carpool.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.logout_24px
import enrutadoseia.composeapp.generated.resources.logout_title
import enrutadoseia.composeapp.generated.resources.profile_driver_section
import enrutadoseia.composeapp.generated.resources.profile_title
import enrutadoseia.composeapp.generated.resources.role_selector_driver_title
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import enrutadoseia.composeapp.generated.resources.routes_list_title
import enrutadoseia.composeapp.generated.resources.vehicles_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToRoutes: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfileEvent.LogoutSuccess -> onLogout()
            ProfileEvent.NavigateToRoutes -> onNavigateToRoutes()
            ProfileEvent.NavigateToVehicles -> onNavigateToVehicles()
        }
    }

    ProfileContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileUiState,
    onAction: (ProfileAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                UserHeader(user = state.user, activeRole = state.activeRole)

                HorizontalDivider()

                if (state.activeRole == UserRole.Driver) {
                    Text(
                        text = stringResource(Res.string.profile_driver_section),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.routes_list_title)) },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.location_on_24px),
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { onAction(ProfileAction.OnMyRoutesClick) }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.vehicles_list_title)) },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.directions_car_24px),
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { onAction(ProfileAction.OnMyVehiclesClick) }
                    )
                    HorizontalDivider()
                }

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.logout_title),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.logout_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.clickable { onAction(ProfileAction.OnLogoutClick) }
                )
            }
        }
    }
}

@Composable
private fun UserHeader(user: User?, activeRole: UserRole?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (user?.name ?: user?.email ?: "?").first().uppercaseChar().toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = user?.name ?: user?.email ?: "",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            if (user?.name != null) {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (activeRole != null) {
                Spacer(modifier = Modifier.height(0.dp))
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = when (activeRole) {
                                UserRole.Driver -> stringResource(Res.string.role_selector_driver_title)
                                UserRole.Passenger -> stringResource(Res.string.role_selector_passenger_title)
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileContentDriverPreview() {
    CarpoolTheme {
        ProfileContent(
            state = ProfileUiState(
                user = User(
                    id = "1",
                    email = "conductor@eia.edu.co",
                    name = "Carlos López",
                    isEmailVerified = true,
                    isPassenger = false,
                    isDriver = true
                ),
                activeRole = UserRole.Driver,
                isLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun ProfileContentPassengerPreview() {
    CarpoolTheme {
        ProfileContent(
            state = ProfileUiState(
                user = User(
                    id = "2",
                    email = "pasajero@eia.edu.co",
                    name = "Ana Martínez",
                    isEmailVerified = true,
                    isPassenger = true,
                    isDriver = false
                ),
                activeRole = UserRole.Passenger,
                isLoading = false
            ),
            onAction = {}
        )
    }
}
