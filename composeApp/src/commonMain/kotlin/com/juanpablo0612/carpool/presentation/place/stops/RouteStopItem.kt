package com.juanpablo0612.carpool.presentation.place.stops

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.juanpablo0612.carpool.presentation.ui.components.Pill
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.lock_24px
import enrutadoseia.composeapp.generated.resources.route_stop_remove
import enrutadoseia.composeapp.generated.resources.select_location_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * One stop on a route timeline. A locked stop with a [lockDescription] shows a lock where the
 * remove button would be, so it reads as fixed rather than silently unresponsive; [tag] is a
 * short note under the place, such as who gets on there.
 */
@Composable
fun RouteStopItem(
    label: String,
    place: Place?,
    isLocked: Boolean,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null,
    showConnector: Boolean = true,
    lockDescription: String? = null,
    tag: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(horizontal = Spacing.screenHorizontal),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline column — 24dp rail width is component-intrinsic, not a spacing value
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp)) // centers the dot on the label's first line
            Box(
                modifier = Modifier
                    .size(12.dp) // dot-intrinsic size
                    .clip(CircleShape)
                    .background(
                        when {
                            // Locked stops get a muted dot regardless of whether a place is set,
                            // so a read-only trajectory doesn't share the vivid primary-colored
                            // dot that signals "tap me" on the editable version.
                            isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            place != null -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )

            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp) // hairline connector
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.lg))

        // Content column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showConnector) Spacing.lg else 0.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.primary
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

            if (tag != null) {
                Pill(
                    text = tag,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
            }
        }

        if (isLocked && lockDescription != null) {
            // Same 48dp slot as the remove button, so locked and removable stops line up.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(48.dp)
                    .semantics { contentDescription = lockDescription },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.lock_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
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
                    contentDescription = stringResource(Res.string.route_stop_remove),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
