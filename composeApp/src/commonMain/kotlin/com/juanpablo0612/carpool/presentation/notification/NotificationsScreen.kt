package com.juanpablo0612.carpool.presentation.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.notification.components.SwipeToDeleteNotification
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.notification.components.NotificationPermissionBanner
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import com.juanpablo0612.carpool.presentation.ui.util.rememberNotificationPermissionState
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notification_delete_confirm_body
import enrutadoseia.composeapp.generated.resources.notification_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.notification_delete_confirm_title
import enrutadoseia.composeapp.generated.resources.notifications_24px
import enrutadoseia.composeapp.generated.resources.notifications_clear_all
import enrutadoseia.composeapp.generated.resources.notifications_clear_all_confirm_body
import enrutadoseia.composeapp.generated.resources.notifications_clear_all_confirm_title
import enrutadoseia.composeapp.generated.resources.notifications_empty_subtitle
import enrutadoseia.composeapp.generated.resources.notifications_empty_title
import enrutadoseia.composeapp.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onBackClick: () -> Unit,
    onNavigateTo: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            NotificationsEvent.NavigateBack -> onBackClick()
            is NotificationsEvent.NavigateTo -> onNavigateTo(event.deepLink)
        }
    }

    val notificationPermission = rememberNotificationPermissionState()
    NotificationsContent(
        state = state,
        onAction = viewModel::onAction,
        showPermissionBanner = !notificationPermission.isGranted,
        onEnableNotifications = notificationPermission::openSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsContent(
    state: NotificationsUiState,
    onAction: (NotificationsAction) -> Unit,
    showPermissionBanner: Boolean = false,
    onEnableNotifications: () -> Unit = {},
) {
    if (state.showClearAllDialog) {
        ConfirmDialog(
            title = stringResource(Res.string.notifications_clear_all_confirm_title),
            description = stringResource(Res.string.notifications_clear_all_confirm_body),
            confirmText = stringResource(Res.string.notifications_clear_all),
            onConfirm = { onAction(NotificationsAction.OnClearAllConfirmed) },
            onDismiss = { onAction(NotificationsAction.OnClearAllDismissed) },
            isDestructive = true
        )
    }

    if (state.pendingDeleteId != null) {
        ConfirmDialog(
            title = stringResource(Res.string.notification_delete_confirm_title),
            description = stringResource(Res.string.notification_delete_confirm_body),
            confirmText = stringResource(Res.string.notification_delete_confirm_button),
            onConfirm = { onAction(NotificationsAction.OnConfirmDelete) },
            onDismiss = { onAction(NotificationsAction.OnDismissDeleteConfirm) },
            isDestructive = true
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.notifications_title),
                onBack = { onAction(NotificationsAction.OnBackClick) },
                actions = {
                    if (state.notifications.isNotEmpty()) {
                        TextButton(onClick = { onAction(NotificationsAction.OnClearAllClick) }) {
                            Text(stringResource(Res.string.notifications_clear_all))
                        }
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showPermissionBanner) {
                NotificationPermissionBanner(
                    onEnable = onEnableNotifications,
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                )
            }

            state.actionError?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                        .clickable { onAction(NotificationsAction.OnDismissActionError) }
                )
            }

            val pullRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(NotificationsAction.Refresh) },
                state = pullRefreshState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        ListSkeleton(modifier = Modifier.fillMaxSize())
                    }
                    state.error != null -> {
                        ErrorState(
                            description = stringResource(state.error.asStringResource()),
                            onRetry = { onAction(NotificationsAction.OnRetry) },
                            modifier = Modifier.fillMaxSize().padding(Spacing.lg)
                        )
                    }
                    state.notifications.isEmpty() -> {
                        EmptyState(
                            icon = vectorResource(Res.drawable.notifications_24px),
                            title = stringResource(Res.string.notifications_empty_title),
                            description = stringResource(Res.string.notifications_empty_subtitle),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        CenteredContent(ContentWidth.list, modifier = Modifier.fillMaxSize(), gutter = 0.dp) { margin ->
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = Spacing.sm).plusHorizontal(margin),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                items(state.notifications, key = { it.id }) { notification ->
                                    SwipeToDeleteNotification(
                                        notification = notification,
                                        actionError = state.actionError is NotificationActionError.DeleteFailed,
                                        isPendingDelete = state.pendingDeleteId == notification.id,
                                        onSwipeToDelete = { onAction(NotificationsAction.OnSwipeToDelete(notification.id)) },
                                        onClick = { onAction(NotificationsAction.OnNotificationClick(notification)) }
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
