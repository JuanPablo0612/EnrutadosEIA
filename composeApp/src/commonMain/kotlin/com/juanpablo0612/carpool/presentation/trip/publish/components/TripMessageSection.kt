package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_message_counter
import enrutadoseia.composeapp.generated.resources.trip_message_placeholder
import enrutadoseia.composeapp.generated.resources.trip_message_section
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TripMessageSection(
    message: String,
    onMessageChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(
            text = stringResource(Res.string.trip_message_section),
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
        )
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            placeholder = { Text(stringResource(Res.string.trip_message_placeholder)) },
            maxLines = 4,
            minLines = 3
        )
        val extendedColors = LocalExtendedColors.current
        Text(
            text = stringResource(Res.string.trip_message_counter, message.length),
            style = MaterialTheme.typography.bodySmall,
            color = when {
                message.length >= MAX_MESSAGE_LENGTH -> MaterialTheme.colorScheme.error
                message.length >= MAX_MESSAGE_LENGTH - WARNING_THRESHOLD -> extendedColors.warning
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
    }
}

private const val MAX_MESSAGE_LENGTH = 140
private const val WARNING_THRESHOLD = 20
