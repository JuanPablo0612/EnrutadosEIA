package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_vehicle_color_required
import enrutadoseia.composeapp.generated.resources.vehicle_color_blue
import enrutadoseia.composeapp.generated.resources.vehicle_color_black
import enrutadoseia.composeapp.generated.resources.vehicle_color_gray
import enrutadoseia.composeapp.generated.resources.vehicle_color_label
import enrutadoseia.composeapp.generated.resources.vehicle_color_other
import enrutadoseia.composeapp.generated.resources.vehicle_color_red
import enrutadoseia.composeapp.generated.resources.vehicle_color_silver
import enrutadoseia.composeapp.generated.resources.vehicle_color_white
import org.jetbrains.compose.resources.stringResource

// 5. Color chips
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun VehicleColorSection(
    color: String,
    isCustomColor: Boolean,
    customColor: String,
    colorError: Boolean,
    onColorSelected: (String) -> Unit,
    onCustomColorChanged: (String) -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_color_label),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = Spacing.sm)
    )
    val colorLabels = listOf(
        "Blanco" to stringResource(Res.string.vehicle_color_white),
        "Negro" to stringResource(Res.string.vehicle_color_black),
        "Gris" to stringResource(Res.string.vehicle_color_gray),
        "Plateado" to stringResource(Res.string.vehicle_color_silver),
        "Rojo" to stringResource(Res.string.vehicle_color_red),
        "Azul" to stringResource(Res.string.vehicle_color_blue),
        "Otro" to stringResource(Res.string.vehicle_color_other),
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        colorLabels.forEach { (value, label) ->
            FilterChip(
                selected = color == value,
                onClick = { onColorSelected(value) },
                label = { Text(label) }
            )
        }
    }
    if (isCustomColor) {
        Spacer(Modifier.height(Spacing.sm))
        CarpoolTextField(
            value = customColor,
            onValueChange = { onCustomColorChanged(it) },
            label = stringResource(Res.string.vehicle_color_other),
            placeholder = "",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            errorMessage = if (colorError) {
                stringResource(Res.string.error_vehicle_color_required)
            } else null
        )
    }
    // Kept as a detached message only for the chip-group case, which has no field and
    // therefore no supportingText slot to attach to. When the custom-colour field is
    // showing, the message lives on the field above instead.
    if (colorError && !isCustomColor) {
        Text(
            text = stringResource(Res.string.error_vehicle_color_required),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = Spacing.xs, top = Spacing.xs)
        )
    }
    Spacer(Modifier.height(Spacing.lg))
}
