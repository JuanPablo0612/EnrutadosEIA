package com.juanpablo0612.carpool.presentation.route.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.route.orderedDays
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        orderedDays.forEach { (day, labelRes) ->
            FilterChip(
                selected = day in selectedDays,
                onClick = { onToggleDay(day) },
                label = {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
