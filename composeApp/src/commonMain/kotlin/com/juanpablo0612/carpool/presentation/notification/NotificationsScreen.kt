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
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.bookmarks_24px
import enrutadoseia.composeapp.generated.resources.error_action_failed
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

    NotificationsContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsContent(
    state: NotificationsUiState,
    onAction: (NotificationsAction) -> Unit
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

    Scaffold(
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
            if (state.actionError) {
                ErrorMessage(
                    message = stringResource(Res.string.error_action_failed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onAction(NotificationsAction.OnDismissActionError) }
                )
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    state.isLoading -> {
                        ListSkeleton(modifier = Modifier.fillMaxSize())
                    }
                    state.error != null -> {
                        ErrorState(
                            description = stringResource(state.error.asStringResource()),
                            onRetry = { onAction(NotificationsAction.OnRetry) },
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }
                    state.notifications.isEmpty() -> {
                        EmptyState(
                            icon = vectorResource(Res.drawable.bookmarks_24px),
                            title = stringResource(Res.string.notifications_empty_title),
                            description = stringResource(Res.string.notifications_empty_subtitle),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.notifications, key = { it.id }) { notification ->
                                SwipeToDeleteNotification(
                                    notification = notification,
                                    onDismiss = { onAction(NotificationsAction.OnDismiss(notification.id)) },
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
