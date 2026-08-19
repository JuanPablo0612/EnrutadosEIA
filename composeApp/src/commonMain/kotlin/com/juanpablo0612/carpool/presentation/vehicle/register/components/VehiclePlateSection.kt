package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.input.ColombianPlateVisualTransformation
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.vehicle_plate_error_format
import enrutadoseia.composeapp.generated.resources.vehicle_plate_label
import enrutadoseia.composeapp.generated.resources.vehicle_plate_placeholder
import org.jetbrains.compose.resources.stringResource

// 6. Plate
@Composable
internal fun VehiclePlateSection(
    plate: String,
    plateError: Boolean,
    onPlateChanged: (String) -> Unit
) {
    CarpoolTextField(
        value = plate,
        onValueChange = { onPlateChanged(it) },
        label = stringResource(Res.string.vehicle_plate_label),
        placeholder = stringResource(Res.string.vehicle_plate_placeholder),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Next
        ),
        visualTransformation = ColombianPlateVisualTransformation(),
        errorMessage = if (plateError) stringResource(Res.string.vehicle_plate_error_format) else null
    )
    Spacer(Modifier.height(Spacing.lg))
}
