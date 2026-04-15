package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.select_location_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

enum class StopType {
    START, MIDDLE, END
}

@Composable
fun RouteStopItem(
    label: String,
    place: Place?,
    type: StopType,
    isLocked: Boolean,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null,
    showConnector: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (place != null) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
            
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showConnector) 16.dp else 0.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) 
                        else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = place?.name ?: stringResource(Res.string.select_location_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = if (place == null) MaterialTheme.colorScheme.onSurfaceVariant 
                        else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            if (place != null && place.address.isNotBlank()) {
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (onRemove != null && !isLocked) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.delete_24px),
                    contentDescription = "Remove stop",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
