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
import enrutadoseia.composeapp.generated.resources.error_vehicle_brand_required
import enrutadoseia.composeapp.generated.resources.vehicle_brand_label
import enrutadoseia.composeapp.generated.resources.vehicle_brand_placeholder
import org.jetbrains.compose.resources.stringResource

// 2. Brand
@Composable
internal fun VehicleBrandSection(
    brand: String,
    brandError: Boolean,
    onBrandChanged: (String) -> Unit
) {
    CarpoolTextField(
        value = brand,
        onValueChange = { onBrandChanged(it) },
        label = stringResource(Res.string.vehicle_brand_label),
        placeholder = stringResource(Res.string.vehicle_brand_placeholder),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        errorMessage = if (brandError) {
            stringResource(Res.string.error_vehicle_brand_required)
        } else null
    )
    Spacer(Modifier.height(Spacing.lg))
}
