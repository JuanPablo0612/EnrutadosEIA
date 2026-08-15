package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (primaryAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            Button(onClick = primaryAction.onClick) {
                Text(primaryAction.label)
            }
        }
        if (secondaryAction != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            OutlinedButton(onClick = secondaryAction.onClick) {
                Text(secondaryAction.label)
            }
        }
    }
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
