package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggled: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.days_of_week_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                FilterChip(
                    selected = selectedDays.contains(day),
                    onClick = { onDayToggled(day) },
                    label = { 
                        Text(
                            text = when(day) {
                                DayOfWeek.MONDAY -> stringResource(Res.string.day_mon)
                                DayOfWeek.TUESDAY -> stringResource(Res.string.day_tue)
                                DayOfWeek.WEDNESDAY -> stringResource(Res.string.day_wed)
                                DayOfWeek.THURSDAY -> stringResource(Res.string.day_thu)
                                DayOfWeek.FRIDAY -> stringResource(Res.string.day_fri)
                                DayOfWeek.SATURDAY -> stringResource(Res.string.day_sat)
                                DayOfWeek.SUNDAY -> stringResource(Res.string.day_sun)
                                else -> ""
                            }
                        )
                    }
                )
            }
        }
    }
}
