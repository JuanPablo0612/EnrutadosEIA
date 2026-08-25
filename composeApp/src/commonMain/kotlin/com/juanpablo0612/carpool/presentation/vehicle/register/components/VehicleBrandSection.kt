package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleUiState
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_vehicle_brand_required
import enrutadoseia.composeapp.generated.resources.vehicle_brand_label
import enrutadoseia.composeapp.generated.resources.vehicle_brand_other
import enrutadoseia.composeapp.generated.resources.vehicle_brand_placeholder
import org.jetbrains.compose.resources.stringResource

// 2. Brand dropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VehicleBrandSection(
    showBrandDropdown: Boolean,
    isCustomBrand: Boolean,
    brand: String,
    brandError: Boolean,
    onToggleBrandDropdown: () -> Unit,
    onBrandSelected: (String) -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_brand_label),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
    ExposedDropdownMenuBox(
        expanded = showBrandDropdown,
        onExpandedChange = { onToggleBrandDropdown() },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (isCustomBrand) "" else brand,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(stringResource(Res.string.vehicle_brand_placeholder)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBrandDropdown) },
            isError = brandError,
            // Only when the error belongs to the dropdown itself; if the user picked
            // "Otro" the message belongs to the custom-brand field below instead.
            supportingText = if (brandError && !isCustomBrand) {
                { Text(stringResource(Res.string.error_vehicle_brand_required)) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = showBrandDropdown,
            onDismissRequest = { onToggleBrandDropdown() }
        ) {
            RegisterVehicleUiState.COMMON_BRANDS.forEach { commonBrand ->
                DropdownMenuItem(
                    text = { Text(commonBrand) },
                    onClick = { onBrandSelected(commonBrand) }
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.vehicle_brand_other)) },
                onClick = { onBrandSelected("Otro") }
            )
        }
    }
    if (isCustomBrand) {
        Spacer(Modifier.height(Spacing.sm))
        CarpoolTextField(
            value = brand,
            onValueChange = { onBrandSelected(it) },
            label = stringResource(Res.string.vehicle_brand_other),
            placeholder = "",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            // The message goes through errorMessage so it lands in the field's
            // supportingText and is announced with the field. It used to be a single
            // space here — just enough to redden the border — with the real text in a
            // detached Text below, which a screen reader never associates with the
            // field it describes.
            errorMessage = if (brandError) {
                stringResource(Res.string.error_vehicle_brand_required)
            } else null
        )
    }
    Spacer(Modifier.height(Spacing.lg))
}
