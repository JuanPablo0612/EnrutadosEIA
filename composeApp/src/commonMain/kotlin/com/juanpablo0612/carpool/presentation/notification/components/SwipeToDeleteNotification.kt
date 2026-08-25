package com.juanpablo0612.carpool.presentation.notification.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.domain.notification.model.AppNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeToDeleteNotification(
    notification: AppNotification,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DeleteNotificationBackground() },
        content = {
            NotificationItem(notification = notification, onClick = onClick)
        }
    )
}
