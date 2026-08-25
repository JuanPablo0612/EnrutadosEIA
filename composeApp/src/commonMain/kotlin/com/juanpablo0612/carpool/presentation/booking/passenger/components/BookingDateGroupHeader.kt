package com.juanpablo0612.carpool.presentation.booking.passenger.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.relative_date_later
import enrutadoseia.composeapp.generated.resources.relative_date_this_week
import enrutadoseia.composeapp.generated.resources.relative_date_today
import enrutadoseia.composeapp.generated.resources.relative_date_tomorrow
import org.jetbrains.compose.resources.stringResource

enum class BookingDateGroup { TODAY, TOMORROW, THIS_WEEK, LATER }

@Composable
fun BookingDateGroupHeader(group: BookingDateGroup, modifier: Modifier = Modifier) {
    Text(
        text = when (group) {
            BookingDateGroup.TODAY -> stringResource(Res.string.relative_date_today)
            BookingDateGroup.TOMORROW -> stringResource(Res.string.relative_date_tomorrow)
            BookingDateGroup.THIS_WEEK -> stringResource(Res.string.relative_date_this_week)
            BookingDateGroup.LATER -> stringResource(Res.string.relative_date_later)
        },
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
    )
}
