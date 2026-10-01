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
import androidx.compose.ui.text.style.TextOverflow
import com.juanpablo0612.carpool.presentation.profile.components.DeleteAccountDialog
import com.juanpablo0612.carpool.presentation.profile.components.ProfileListItem
import com.juanpablo0612.carpool.presentation.profile.components.UserHeader
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.TopBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
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
import enrutadoseia.composeapp.generated.resources.profile_config_section
import enrutadoseia.composeapp.generated.resources.profile_delete_account
import enrutadoseia.composeapp.generated.resources.profile_my_account_section
import enrutadoseia.composeapp.generated.resources.profile_notifications_settings
import enrutadoseia.composeapp.generated.resources.profile_saved_places
import enrutadoseia.composeapp.generated.resources.profile_title
import enrutadoseia.composeapp.generated.resources.routes_list_title
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
    onLogout: () -> Unit,
    onDeleteAccountSuccess: () -> Unit,
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
            ProfileEvent.DeleteAccountSuccess -> onDeleteAccountSuccess()
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

    if (state.showDeleteAccountDialog) {
        val trimmedName = state.user?.name?.takeIf { it.isNotBlank() }
        DeleteAccountDialog(
            nameInput = state.deleteAccountNameInput,
            expectedName = trimmedName ?: state.user?.email ?: "",
            usingEmailFallback = trimmedName == null,
            isLoading = state.isDeleting,
            error = state.deleteAccountError,
            onNameChange = { onAction(ProfileAction.OnDeleteAccountNameChange(it)) },
            onConfirm = { onAction(ProfileAction.OnDeleteAccountConfirmed) },
            onDismiss = { onAction(ProfileAction.OnDeleteAccountDismissed) }
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            TopAppBar(
                windowInsets = TopBarInsets,
                title = {
                    Text(
                        text = stringResource(Res.string.profile_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                    .centeredContent(ContentWidth.list)
            ) {
                UserHeader(
                    user = state.user,
                    onEditClick = { onAction(ProfileAction.OnEditProfileClick) }
                )

                HorizontalDivider()

                SectionHeader(stringResource(Res.string.profile_my_account_section))
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
                ProfileListItem(
                    title = stringResource(Res.string.profile_saved_places),
                    icon = { Icon(vectorResource(Res.drawable.location_on_24px), null) },
                    onClick = { onAction(ProfileAction.OnSavedPlacesClick) }
                )

                HorizontalDivider()

                SectionHeader(stringResource(Res.string.profile_config_section))
                ProfileListItem(
                    title = stringResource(Res.string.profile_notifications_settings),
                    icon = { Icon(vectorResource(Res.drawable.notifications_24px), null) },
                    onClick = { onAction(ProfileAction.OnNotificationsClick) }
                )
                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    OutlinedButton(
                        onClick = { onAction(ProfileAction.OnLogoutClick) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(vectorResource(Res.drawable.logout_24px), null)
                        Spacer(Modifier.size(Spacing.sm))
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

                Spacer(Modifier.height(Spacing.lg))
            }
        }
    }
}

