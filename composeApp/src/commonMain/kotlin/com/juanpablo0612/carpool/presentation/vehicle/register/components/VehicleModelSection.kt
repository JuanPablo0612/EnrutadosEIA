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
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_vehicle_model_required
import enrutadoseia.composeapp.generated.resources.vehicle_model_label
import enrutadoseia.composeapp.generated.resources.vehicle_model_placeholder
import org.jetbrains.compose.resources.stringResource

// 3. Model
@Composable
internal fun VehicleModelSection(
    model: String,
    modelError: Boolean,
    onModelChanged: (String) -> Unit
) {
    CarpoolTextField(
        value = model,
        onValueChange = { onModelChanged(it) },
        label = stringResource(Res.string.vehicle_model_label),
        placeholder = stringResource(Res.string.vehicle_model_placeholder),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        // Previously a bare space with no message anywhere: the field reddened and
        // the reason was given in no modality at all, visual or spoken.
        errorMessage = if (modelError) {
            stringResource(Res.string.error_vehicle_model_required)
        } else null
    )
    Spacer(Modifier.height(Spacing.lg))
}
