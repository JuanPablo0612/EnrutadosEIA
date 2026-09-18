package com.juanpablo0612.carpool.presentation.booking.passenger.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.RelativeDateGroup
import com.juanpablo0612.carpool.presentation.ui.util.label

@Composable
fun BookingDateGroupHeader(group: RelativeDateGroup, modifier: Modifier = Modifier) {
    Text(
        text = group.label(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
    )
}
