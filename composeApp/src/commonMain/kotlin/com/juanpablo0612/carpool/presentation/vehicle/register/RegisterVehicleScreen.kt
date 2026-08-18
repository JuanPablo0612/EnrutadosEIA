package com.juanpablo0612.carpool.presentation.vehicle.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.domain.vehicle.model.VehicleType
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Alpha
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.edit_vehicle_title
import enrutadoseia.composeapp.generated.resources.error_vehicle_brand_required
import enrutadoseia.composeapp.generated.resources.error_vehicle_color_required
import enrutadoseia.composeapp.generated.resources.error_vehicle_model_required
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_vehicle_title
import enrutadoseia.composeapp.generated.resources.vehicle_brand_label
import enrutadoseia.composeapp.generated.resources.vehicle_brand_other
import enrutadoseia.composeapp.generated.resources.vehicle_brand_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_change_photo
import enrutadoseia.composeapp.generated.resources.vehicle_color_blue
import enrutadoseia.composeapp.generated.resources.vehicle_color_black
import enrutadoseia.composeapp.generated.resources.vehicle_color_gray
import enrutadoseia.composeapp.generated.resources.vehicle_color_label
import enrutadoseia.composeapp.generated.resources.vehicle_color_other
import enrutadoseia.composeapp.generated.resources.vehicle_color_red
import enrutadoseia.composeapp.generated.resources.vehicle_color_silver
import enrutadoseia.composeapp.generated.resources.vehicle_color_white
import enrutadoseia.composeapp.generated.resources.vehicle_model_label
import enrutadoseia.composeapp.generated.resources.vehicle_model_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_photo_choose_gallery
import enrutadoseia.composeapp.generated.resources.vehicle_photo_hint
import enrutadoseia.composeapp.generated.resources.vehicle_photo_section
import enrutadoseia.composeapp.generated.resources.vehicle_photo_take_photo
import enrutadoseia.composeapp.generated.resources.vehicle_photo_tap_to_add
import enrutadoseia.composeapp.generated.resources.vehicle_plate_error_format
import enrutadoseia.composeapp.generated.resources.vehicle_plate_label
import enrutadoseia.composeapp.generated.resources.vehicle_plate_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_save_button
import enrutadoseia.composeapp.generated.resources.vehicle_seats_helper
import enrutadoseia.composeapp.generated.resources.vehicle_seats_label
import enrutadoseia.composeapp.generated.resources.vehicle_type_hatchback
import enrutadoseia.composeapp.generated.resources.vehicle_type_label
import enrutadoseia.composeapp.generated.resources.vehicle_type_other
import enrutadoseia.composeapp.generated.resources.vehicle_type_pickup
import enrutadoseia.composeapp.generated.resources.vehicle_type_sedan
import enrutadoseia.composeapp.generated.resources.vehicle_type_suv
import enrutadoseia.composeapp.generated.resources.vehicle_update_button
import enrutadoseia.composeapp.generated.resources.vehicle_year_label
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RegisterVehicleScreen(
    viewModel: RegisterVehicleViewModel,
    onBackClick: () -> Unit,
    onVehicleRegistered: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RegisterVehicleEvent.VehicleRegistered -> onVehicleRegistered()
            RegisterVehicleEvent.NavigateBack -> onBackClick()
        }
    }

    RegisterVehicleContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterVehicleContent(
    state: RegisterVehicleUiState,
    onAction: (RegisterVehicleAction) -> Unit
) {
    val photoPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) onAction(RegisterVehicleAction.OnPhotoSelected(file))
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        if (file != null) onAction(RegisterVehicleAction.OnPhotoSelected(file))
    }

    // Photo source bottom sheet
    if (state.showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(RegisterVehicleAction.OnDismissPhotoSheet) },
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontalForm)
                    // Bottom-sheet safe-area padding: 48dp isn't on the spacing scale, so it's
                    // expressed as two xl steps rather than approximated to a single token.
                    .padding(bottom = Spacing.xl + Spacing.xl)
            ) {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = Spacing.xl)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onAction(RegisterVehicleAction.OnDismissPhotoSheet)
                            cameraLauncher.launch()
                        }
                        .padding(vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.photo_camera_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(Spacing.lg))
                    Text(
                        stringResource(Res.string.vehicle_photo_take_photo),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onAction(RegisterVehicleAction.OnDismissPhotoSheet)
                            photoPicker.launch()
                        }
                        .padding(vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(Spacing.lg))
                    Text(
                        stringResource(Res.string.vehicle_photo_choose_gallery),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                title = if (state.mode == RegisterVehicleUiState.Mode.Edit)
                    stringResource(Res.string.edit_vehicle_title)
                else
                    stringResource(Res.string.register_vehicle_title),
                onBackClick = { onAction(RegisterVehicleAction.OnBackClick) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(horizontal = Spacing.screenHorizontalForm, vertical = Spacing.lg),
        ) {

            // 1. Photo
            item {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // component-intrinsic: fixed photo-preview height, not a spacing value
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .clickable { onAction(RegisterVehicleAction.OnShowPhotoSheet) },
                    contentAlignment = Alignment.Center
                ) {
                    val hasPhoto = state.photoFile != null || state.existingPhotoUrl != null
                    if (hasPhoto) {
                        AsyncImage(
                            model = state.photoFile ?: state.existingPhotoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = Alpha.SCRIM))
                                .padding(vertical = Spacing.sm),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.vehicle_change_photo),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(Spacing.lg)
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.photo_camera_24px),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp), // component-intrinsic icon size
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text = stringResource(Res.string.vehicle_photo_tap_to_add),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = stringResource(Res.string.vehicle_photo_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // 2. Brand dropdown
            item {
                Text(
                    text = stringResource(Res.string.vehicle_brand_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                ExposedDropdownMenuBox(
                    expanded = state.showBrandDropdown,
                    onExpandedChange = { onAction(RegisterVehicleAction.OnToggleBrandDropdown) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (state.isCustomBrand) "" else state.brand,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(stringResource(Res.string.vehicle_brand_placeholder)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showBrandDropdown) },
                        isError = state.brandError,
                        // Only when the error belongs to the dropdown itself; if the user picked
                        // "Otro" the message belongs to the custom-brand field below instead.
                        supportingText = if (state.brandError && !state.isCustomBrand) {
                            { Text(stringResource(Res.string.error_vehicle_brand_required)) }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = state.showBrandDropdown,
                        onDismissRequest = { onAction(RegisterVehicleAction.OnToggleBrandDropdown) }
                    ) {
                        RegisterVehicleUiState.COMMON_BRANDS.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand) },
                                onClick = { onAction(RegisterVehicleAction.OnBrandSelected(brand)) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.vehicle_brand_other)) },
                            onClick = { onAction(RegisterVehicleAction.OnBrandSelected("Otro")) }
                        )
                    }
                }
                if (state.isCustomBrand) {
                    Spacer(Modifier.height(Spacing.sm))
                    CarpoolTextField(
                        value = state.brand,
                        onValueChange = { onAction(RegisterVehicleAction.OnBrandSelected(it)) },
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
                        errorMessage = if (state.brandError) {
                            stringResource(Res.string.error_vehicle_brand_required)
                        } else null
                    )
                }
                Spacer(Modifier.height(Spacing.lg))
            }

            // 3. Model
            item {
                CarpoolTextField(
                    value = state.model,
                    onValueChange = { onAction(RegisterVehicleAction.OnModelChanged(it)) },
                    label = stringResource(Res.string.vehicle_model_label),
                    placeholder = stringResource(Res.string.vehicle_model_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    // Previously a bare space with no message anywhere: the field reddened and
                    // the reason was given in no modality at all, visual or spoken.
                    errorMessage = if (state.modelError) {
                        stringResource(Res.string.error_vehicle_model_required)
                    } else null
                )
                Spacer(Modifier.height(Spacing.lg))
            }

            // 4. Year dropdown
            item {
                Text(
                    text = stringResource(Res.string.vehicle_year_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                val currentYear = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date.year
                val years = (currentYear + 1 downTo currentYear - 30).toList()
                ExposedDropdownMenuBox(
                    expanded = state.showYearDropdown,
                    onExpandedChange = { onAction(RegisterVehicleAction.OnToggleYearDropdown) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.year.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showYearDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = state.showYearDropdown,
                        onDismissRequest = { onAction(RegisterVehicleAction.OnToggleYearDropdown) }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = { onAction(RegisterVehicleAction.OnYearSelected(year)) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.lg))
            }

            // 5. Color chips
            item {
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
                            selected = state.color == value,
                            onClick = { onAction(RegisterVehicleAction.OnColorSelected(value)) },
                            label = { Text(label) }
                        )
                    }
                }
                if (state.isCustomColor) {
                    Spacer(Modifier.height(Spacing.sm))
                    CarpoolTextField(
                        value = state.customColor,
                        onValueChange = { onAction(RegisterVehicleAction.OnCustomColorChanged(it)) },
                        label = stringResource(Res.string.vehicle_color_other),
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        errorMessage = if (state.colorError) {
                            stringResource(Res.string.error_vehicle_color_required)
                        } else null
                    )
                }
                // Kept as a detached message only for the chip-group case, which has no field and
                // therefore no supportingText slot to attach to. When the custom-colour field is
                // showing, the message lives on the field above instead.
                if (state.colorError && !state.isCustomColor) {
                    Text(
                        text = stringResource(Res.string.error_vehicle_color_required),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = Spacing.xs, top = Spacing.xs)
                    )
                }
                Spacer(Modifier.height(Spacing.lg))
            }

            // 6. Plate
            item {
                CarpoolTextField(
                    value = state.plate,
                    onValueChange = { onAction(RegisterVehicleAction.OnPlateChanged(it)) },
                    label = stringResource(Res.string.vehicle_plate_label),
                    placeholder = stringResource(Res.string.vehicle_plate_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = ColombianPlateVisualTransformation(),
                    errorMessage = if (state.plateError) stringResource(Res.string.vehicle_plate_error_format) else null
                )
                Spacer(Modifier.height(Spacing.lg))
            }

            // 7. Seat count stepper
            item {
                Text(
                    text = stringResource(Res.string.vehicle_seats_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                NumberStepper(
                    value = state.seatCount,
                    onChange = { onAction(RegisterVehicleAction.OnSeatCountChanged(it)) },
                    min = 1,
                    max = 7
                )
                Text(
                    text = stringResource(Res.string.vehicle_seats_helper),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
                Spacer(Modifier.height(Spacing.lg))
            }

            // 8. Vehicle type (optional)
            item {
                Text(
                    text = stringResource(Res.string.vehicle_type_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = Spacing.sm)
                )
                val typeEntries = listOf(
                    VehicleType.Sedan to stringResource(Res.string.vehicle_type_sedan),
                    VehicleType.Hatchback to stringResource(Res.string.vehicle_type_hatchback),
                    VehicleType.SUV to stringResource(Res.string.vehicle_type_suv),
                    VehicleType.Pickup to stringResource(Res.string.vehicle_type_pickup),
                    VehicleType.Other to stringResource(Res.string.vehicle_type_other),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    typeEntries.forEach { (type, label) ->
                        FilterChip(
                            selected = state.type == type,
                            onClick = { onAction(RegisterVehicleAction.OnTypeSelected(type)) },
                            label = { Text(label) }
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.lg))
            }

            // General error
            item {
                if (state.generalError != null) {
                    ErrorMessage(message = stringResource(state.generalError.asStringResource()))
                    Spacer(Modifier.height(Spacing.lg))
                }
            }

            // Save button
            item {
                Spacer(Modifier.height(Spacing.sm))
                PrimaryButton(
                    text = if (state.mode == RegisterVehicleUiState.Mode.Edit)
                        stringResource(Res.string.vehicle_update_button)
                    else
                        stringResource(Res.string.vehicle_save_button),
                    onClick = { onAction(RegisterVehicleAction.OnSaveClick) },
                    enabled = state.isValid,
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}

@Preview
@Composable
private fun RegisterVehicleCreatePreview() {
    CarpoolTheme {
        RegisterVehicleContent(
            state = RegisterVehicleUiState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun RegisterVehicleEditPreview() {
    CarpoolTheme {
        RegisterVehicleContent(
            state = RegisterVehicleUiState(
                mode = RegisterVehicleUiState.Mode.Edit,
                brand = "Toyota",
                model = "Corolla",
                plate = "ABC123",
                color = "Blanco",
                year = 2020,
                seatCount = 3
            ),
            onAction = {}
        )
    }
}
