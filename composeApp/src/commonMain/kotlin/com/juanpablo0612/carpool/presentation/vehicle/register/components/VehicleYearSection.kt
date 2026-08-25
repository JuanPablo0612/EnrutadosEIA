package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.vehicle_year_label
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

// 4. Year dropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VehicleYearSection(
    showYearDropdown: Boolean,
    year: Int,
    onToggleYearDropdown: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_year_label),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
    val currentYear = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.year
    val years = (currentYear + 1 downTo currentYear - 30).toList()
    ExposedDropdownMenuBox(
        expanded = showYearDropdown,
        onExpandedChange = { onToggleYearDropdown() },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = year.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showYearDropdown) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = showYearDropdown,
            onDismissRequest = { onToggleYearDropdown() }
        ) {
            years.forEach { yearOption ->
                DropdownMenuItem(
                    text = { Text(yearOption.toString()) },
                    onClick = { onYearSelected(yearOption) }
                )
            }
        }
    }
    Spacer(Modifier.height(Spacing.lg))
}
