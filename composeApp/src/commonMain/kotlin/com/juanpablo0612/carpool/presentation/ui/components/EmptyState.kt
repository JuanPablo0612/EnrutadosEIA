package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.inbox_24px
import org.jetbrains.compose.resources.vectorResource

data class ActionButton(val label: String, val onClick: () -> Unit)

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    primaryAction: ActionButton? = null,
    secondaryAction: ActionButton? = null,
) {
    StateMessage(
        icon = icon,
        iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
        title = title,
        description = description,
        modifier = modifier,
        actions = if (primaryAction == null && secondaryAction == null) null else {
            {
                primaryAction?.let { PrimaryButton(text = it.label, onClick = it.onClick) }
                secondaryAction?.let { SecondaryButton(text = it.label, onClick = it.onClick) }
            }
        },
    )
}

@Preview
@Composable
private fun EmptyStatePreview() {
    CarpoolTheme {
        EmptyState(
            icon = vectorResource(Res.drawable.inbox_24px),
            title = "No trips yet",
            description = "Trips you publish will show up here.",
            primaryAction = ActionButton(label = "Publish a trip", onClick = {}),
            secondaryAction = ActionButton(label = "Learn more", onClick = {}),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
