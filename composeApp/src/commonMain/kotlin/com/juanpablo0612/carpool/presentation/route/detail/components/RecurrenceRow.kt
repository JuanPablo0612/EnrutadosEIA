package com.juanpablo0612.carpool.presentation.route.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.day_abbr_fri
import enrutadoseia.composeapp.generated.resources.day_abbr_mon
import enrutadoseia.composeapp.generated.resources.day_abbr_sat
import enrutadoseia.composeapp.generated.resources.day_abbr_sun
import enrutadoseia.composeapp.generated.resources.day_abbr_thu
import enrutadoseia.composeapp.generated.resources.day_abbr_tue
import enrutadoseia.composeapp.generated.resources.day_abbr_wed
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RecurrenceRow(
    recurringDays: Set<DayOfWeek>,
    typicalDepartureTime: LocalTime?
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        val dayAbbrevs = mapOf(
            DayOfWeek.MONDAY to Res.string.day_abbr_mon,
            DayOfWeek.TUESDAY to Res.string.day_abbr_tue,
            DayOfWeek.WEDNESDAY to Res.string.day_abbr_wed,
            DayOfWeek.THURSDAY to Res.string.day_abbr_thu,
            DayOfWeek.FRIDAY to Res.string.day_abbr_fri,
            DayOfWeek.SATURDAY to Res.string.day_abbr_sat,
            DayOfWeek.SUNDAY to Res.string.day_abbr_sun
        )
        recurringDays.sortedBy { it.ordinal }.forEach { day ->
            dayAbbrevs[day]?.let { res ->
                Text(
                    text = stringResource(res),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        typicalDepartureTime?.let { t ->
            Text(
                text = "· ${t.hour.toString().padStart(2, '0')}:${t.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
