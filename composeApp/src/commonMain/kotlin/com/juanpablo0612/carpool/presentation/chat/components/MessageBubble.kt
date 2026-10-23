package com.juanpablo0612.carpool.presentation.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.chat.ChatMessageItem
import com.juanpablo0612.carpool.presentation.chat.MessageStatus
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chat_message_not_sent
import enrutadoseia.composeapp.generated.resources.chat_message_sending
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

/**
 * One message. A failed outgoing one says so right under the bubble, and the whole group is the
 * retry button, so the problem and its fix sit next to the message they concern.
 */
@Composable
internal fun MessageBubble(item: ChatMessageItem, isOwn: Boolean, maxWidth: Dp, onRetry: () -> Unit) {
    val failed = item.status == MessageStatus.Failed
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (failed) Modifier.clickable(role = Role.Button, onClick = onRetry) else Modifier),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        val contentColor = if (isOwn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwn) 16.dp else 4.dp,
                bottomEnd = if (isOwn) 4.dp else 16.dp
            ),
            color = if (isOwn) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = maxWidth)
        ) {
            Column(modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)) {
                Text(
                    text = item.message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
                Text(
                    text = if (item.status == MessageStatus.Sending) {
                        stringResource(Res.string.chat_message_sending)
                    } else {
                        formatTimestamp(item.message.timestamp)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        if (failed) {
            Text(
                text = stringResource(Res.string.chat_message_not_sent),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun formatTimestamp(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}
