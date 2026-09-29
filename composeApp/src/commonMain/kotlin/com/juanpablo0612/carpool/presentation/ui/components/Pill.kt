package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * A small, non-interactive label on a tinted pill: a trip's role ("Conduces"), a count, a
 * "Principal" marker. Pass a paired container/content role so the text keeps its contrast.
 * For booking and trip states use [BookingStatusBadge] / [TripStatusBadge] instead.
 */
@Composable
fun Pill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Surface(shape = CircleShape, color = containerColor, contentColor = contentColor, modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelMedium)
        }
    }
}
