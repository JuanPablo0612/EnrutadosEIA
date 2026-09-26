package com.juanpablo0612.carpool.presentation.notification.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.juanpablo0612.carpool.domain.notification.model.AppNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeToDeleteNotification(
    notification: AppNotification,
    actionError: Boolean,
    isPendingDelete: Boolean,
    onSwipeToDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            // Never auto-commit: a swipe only requests the confirm dialog, like every other
            // destructive action in the app.
            // The row snaps back below whenever the delete isn't actually confirmed.
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onSwipeToDelete()
            }
            false
        }
    )

    // Snap the row back once there's no pending confirmation for it left — either the user
    // cancelled, or the delete failed and there's nothing further to show mid-swipe for.
    LaunchedEffect(isPendingDelete, actionError) {
        if (!isPendingDelete || actionError) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DeleteNotificationBackground() },
        content = {
            NotificationItem(notification = notification, onClick = onClick)
        }
    )
}
