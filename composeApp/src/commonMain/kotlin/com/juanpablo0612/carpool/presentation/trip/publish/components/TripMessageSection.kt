package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_message_counter
import enrutadoseia.composeapp.generated.resources.trip_message_placeholder
import enrutadoseia.composeapp.generated.resources.trip_message_section
import org.jetbrains.compose.resources.stringResource

/** An optional note for passengers, with a counter that warns as it nears the limit. */
@Composable
internal fun TripMessageSection(
    message: String,
    onMessageChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extendedColors = LocalExtendedColors.current
    CarpoolTextField(
        value = message,
        onValueChange = onMessageChange,
        label = stringResource(Res.string.trip_message_section),
        placeholder = stringResource(Res.string.trip_message_placeholder),
        singleLine = false,
        minLines = 3,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        supportingText = {
            Text(
                text = stringResource(Res.string.trip_message_counter, message.length),
                color = when {
                    message.length >= MAX_MESSAGE_LENGTH -> MaterialTheme.colorScheme.error
                    message.length >= MAX_MESSAGE_LENGTH - WARNING_THRESHOLD -> extendedColors.warning
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = modifier,
    )
}

private const val MAX_MESSAGE_LENGTH = 140
private const val WARNING_THRESHOLD = 20
