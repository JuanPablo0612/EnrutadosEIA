package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_24px
import enrutadoseia.composeapp.generated.resources.error_state_retry
import enrutadoseia.composeapp.generated.resources.error_state_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ErrorState(
    description: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.error_state_title),
) {
    StateMessage(
        icon = vectorResource(Res.drawable.error_24px),
        iconContainerColor = MaterialTheme.colorScheme.errorContainer,
        iconTint = MaterialTheme.colorScheme.onErrorContainer,
        title = title,
        description = description,
        modifier = modifier,
        actions = {
            PrimaryButton(text = stringResource(Res.string.error_state_retry), onClick = onRetry)
        },
    )
}

@Preview
@Composable
private fun ErrorStatePreview() {
    CarpoolTheme {
        ErrorState(
            description = "We couldn't load your trips. Check your connection and try again.",
            onRetry = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
