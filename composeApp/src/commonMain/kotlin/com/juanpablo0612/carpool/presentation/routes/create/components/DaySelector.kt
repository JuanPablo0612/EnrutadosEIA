package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.day_abbr_fri
import enrutadoseia.composeapp.generated.resources.day_abbr_mon
import enrutadoseia.composeapp.generated.resources.day_abbr_sat
import enrutadoseia.composeapp.generated.resources.day_abbr_sun
import enrutadoseia.composeapp.generated.resources.day_abbr_thu
import enrutadoseia.composeapp.generated.resources.day_abbr_tue
import enrutadoseia.composeapp.generated.resources.day_abbr_wed
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val orderedDays: List<Pair<DayOfWeek, StringResource>> = listOf(
    DayOfWeek.MONDAY to Res.string.day_abbr_mon,
    DayOfWeek.TUESDAY to Res.string.day_abbr_tue,
    DayOfWeek.WEDNESDAY to Res.string.day_abbr_wed,
    DayOfWeek.THURSDAY to Res.string.day_abbr_thu,
    DayOfWeek.FRIDAY to Res.string.day_abbr_fri,
    DayOfWeek.SATURDAY to Res.string.day_abbr_sat,
    DayOfWeek.SUNDAY to Res.string.day_abbr_sun
)

@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
