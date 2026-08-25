package com.juanpablo0612.carpool.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.profile.components.ActiveRolesDialog
import com.juanpablo0612.carpool.presentation.profile.components.DeleteAccountDialog
import com.juanpablo0612.carpool.presentation.profile.components.ProfileListItem
import com.juanpablo0612.carpool.presentation.profile.components.SectionHeader
import com.juanpablo0612.carpool.presentation.profile.components.UserHeader
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_road_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.logout_24px
import enrutadoseia.composeapp.generated.resources.logout_confirm_button
import enrutadoseia.composeapp.generated.resources.notifications_24px
import enrutadoseia.composeapp.generated.resources.logout_confirm_description
import enrutadoseia.composeapp.generated.resources.logout_confirm_title
import enrutadoseia.composeapp.generated.resources.logout_title
import enrutadoseia.composeapp.generated.resources.profile_active_roles
import enrutadoseia.composeapp.generated.resources.profile_config_section
import enrutadoseia.composeapp.generated.resources.profile_delete_account
import enrutadoseia.composeapp.generated.resources.profile_language
import enrutadoseia.composeapp.generated.resources.profile_language_value
import enrutadoseia.composeapp.generated.resources.profile_my_account_section
import enrutadoseia.composeapp.generated.resources.profile_notifications_settings
import enrutadoseia.composeapp.generated.resources.profile_safety
import enrutadoseia.composeapp.generated.resources.profile_saved_places
import enrutadoseia.composeapp.generated.resources.profile_theme
import enrutadoseia.composeapp.generated.resources.profile_theme_value
import enrutadoseia.composeapp.generated.resources.profile_title
import enrutadoseia.composeapp.generated.resources.routes_list_title
import enrutadoseia.composeapp.generated.resources.swap_horiz_24px
import enrutadoseia.composeapp.generated.resources.shield_24px
import enrutadoseia.composeapp.generated.resources.vehicles_list_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToRoutes: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onNavigateToSavedPlaces: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccountSuccess: () -> Unit,
    onRoleSwitched: (UserRole) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfileEvent.LogoutSuccess -> onLogout()
            ProfileEvent.NavigateToRoutes -> onNavigateToRoutes()
            ProfileEvent.NavigateToVehicles -> onNavigateToVehicles()
            ProfileEvent.NavigateToEditProfile -> onNavigateToEditProfile()
            ProfileEvent.NavigateToSavedPlaces -> onNavigateToSavedPlaces()
            ProfileEvent.NavigateToNotifications -> onNavigateToNotifications()
            ProfileEvent.NavigateToSafety -> onNavigateToSafety()
            ProfileEvent.DeleteAccountSuccess -> onDeleteAccountSuccess()
            is ProfileEvent.RoleSwitched -> onRoleSwitched(event.role)
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
    if (state.showLogoutDialog) {
        ConfirmDialog(
            title = stringResource(Res.string.logout_confirm_title),
            description = stringResource(Res.string.logout_confirm_description),
            confirmText = stringResource(Res.string.logout_confirm_button),
            onConfirm = { onAction(ProfileAction.OnLogoutConfirmed) },
            onDismiss = { onAction(ProfileAction.OnLogoutDismissed) },
            isDestructive = true
        )
    }

    if (state.showActiveRolesDialog) {
        ActiveRolesDialog(
            user = state.user,
            blocked = state.blockedRoleToggle,
            onToggleRole = { role, enabled -> onAction(ProfileAction.OnToggleRole(role, enabled)) },
            onDismiss = { onAction(ProfileAction.OnActiveRolesDismissed) }
        )
    }

    if (state.showDeleteAccountDialog) {
        DeleteAccountDialog(
            nameInput = state.deleteAccountNameInput,
            expectedName = state.user?.name ?: "",
            isLoading = state.isDeleting,
            error = state.deleteAccountError,
            onNameChange = { onAction(ProfileAction.OnDeleteAccountNameChange(it)) },
            onConfirm = { onAction(ProfileAction.OnDeleteAccountConfirmed) },
            onDismiss = { onAction(ProfileAction.OnDeleteAccountDismissed) }
        )
    }

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
            ListSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                UserHeader(
                    user = state.user,
                    onEditClick = { onAction(ProfileAction.OnEditProfileClick) }
                )

                HorizontalDivider()

                SectionHeader(stringResource(Res.string.profile_my_account_section))
                if (state.user?.isDriver == true) {
                    ProfileListItem(
                        title = stringResource(Res.string.vehicles_list_title),
                        icon = { Icon(vectorResource(Res.drawable.directions_car_24px), null) },
                        onClick = { onAction(ProfileAction.OnMyVehiclesClick) }
                    )
                    ProfileListItem(
                        title = stringResource(Res.string.routes_list_title),
                        icon = { Icon(vectorResource(Res.drawable.add_road_24px), null) },
                        onClick = { onAction(ProfileAction.OnMyRoutesClick) }
                    )
                }
                ProfileListItem(
                    title = stringResource(Res.string.profile_saved_places),
                    icon = { Icon(vectorResource(Res.drawable.location_on_24px), null) },
                    onClick = { onAction(ProfileAction.OnSavedPlacesClick) }
                )
                ProfileListItem(
                    title = stringResource(Res.string.profile_active_roles),
                    icon = { Icon(vectorResource(Res.drawable.swap_horiz_24px), null) },
                    onClick = { onAction(ProfileAction.OnActiveRolesClick) }
                )

                HorizontalDivider()

                SectionHeader(stringResource(Res.string.profile_config_section))
                ProfileListItem(
                    title = stringResource(Res.string.profile_notifications_settings),
                    icon = { Icon(vectorResource(Res.drawable.notifications_24px), null) },
                    onClick = { onAction(ProfileAction.OnNotificationsClick) }
                )
                ProfileListItem(
                    title = stringResource(Res.string.profile_safety),
                    icon = { Icon(vectorResource(Res.drawable.shield_24px), null) },
                    onClick = { onAction(ProfileAction.OnSafetyClick) }
                )
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.profile_language)) },
                    trailingContent = {
                        Text(
                            text = stringResource(Res.string.profile_language_value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.profile_theme)) },
                    trailingContent = {
                        Text(
                            text = stringResource(Res.string.profile_theme_value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAction(ProfileAction.OnLogoutClick) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(vectorResource(Res.drawable.logout_24px), null)
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(Res.string.logout_title))
                    }
                    TextButton(
                        onClick = { onAction(ProfileAction.OnDeleteAccountClick) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(Res.string.profile_delete_account))
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

