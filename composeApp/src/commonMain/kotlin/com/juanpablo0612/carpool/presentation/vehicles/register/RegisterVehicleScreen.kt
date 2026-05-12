package com.juanpablo0612.carpool.presentation.vehicles.register

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
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
import com.juanpablo0612.carpool.domain.vehicles.model.VehicleType
import com.juanpablo0612.carpool.presentation.ui.components.AuthTextField
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.edit_vehicle_title
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
import enrutadoseia.composeapp.generated.resources.vehicle_docs_description
import enrutadoseia.composeapp.generated.resources.vehicle_docs_hide
import enrutadoseia.composeapp.generated.resources.vehicle_docs_section
import enrutadoseia.composeapp.generated.resources.vehicle_docs_show
import enrutadoseia.composeapp.generated.resources.vehicle_model_label
import enrutadoseia.composeapp.generated.resources.vehicle_model_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_photo_choose_gallery
import enrutadoseia.composeapp.generated.resources.vehicle_photo_hint
import enrutadoseia.composeapp.generated.resources.vehicle_photo_section
import enrutadoseia.composeapp.generated.resources.vehicle_photo_take_photo
import enrutadoseia.composeapp.generated.resources.vehicle_photo_tap_to_add
import enrutadoseia.composeapp.generated.resources.vehicle_plate_error_format
import enrutadoseia.composeapp.generated.resources.vehicle_plate_label
import enrutadoseia.composeapp.generated.resources.vehicle_save_button
import enrutadoseia.composeapp.generated.resources.vehicle_seats_helper
import enrutadoseia.composeapp.generated.resources.vehicle_seats_label
import enrutadoseia.composeapp.generated.resources.vehicle_soat_label
import enrutadoseia.composeapp.generated.resources.vehicle_tecno_label
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
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

    // Date pickers
    if (state.showSoatDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(RegisterVehicleAction.OnDismissSoatDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    val ms = pickerState.selectedDateMillis
                    if (ms != null) {
                        val date = Instant.fromEpochMilliseconds(ms)
                            .toLocalDateTime(TimeZone.UTC).date
                        onAction(RegisterVehicleAction.OnSoatDateSelected(date))
                    } else {
                        onAction(RegisterVehicleAction.OnDismissSoatDatePicker)
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RegisterVehicleAction.OnDismissSoatDatePicker) }) {
                    Text("Cancelar")
                }
            }
        ) { DatePicker(state = pickerState) }
    }

    if (state.showTecnoDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(RegisterVehicleAction.OnDismissTecnoDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    val ms = pickerState.selectedDateMillis
                    if (ms != null) {
                        val date = Instant.fromEpochMilliseconds(ms)
                            .toLocalDateTime(TimeZone.UTC).date
                        onAction(RegisterVehicleAction.OnTecnoDateSelected(date))
                    } else {
                        onAction(RegisterVehicleAction.OnDismissTecnoDatePicker)
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RegisterVehicleAction.OnDismissTecnoDatePicker) }) {
                    Text("Cancelar")
                }
            }
        ) { DatePicker(state = pickerState) }
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
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onAction(RegisterVehicleAction.OnDismissPhotoSheet)
                            cameraLauncher.launch()
                        }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.photo_camera_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        stringResource(Res.string.vehicle_photo_take_photo),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onAction(RegisterVehicleAction.OnDismissPhotoSheet)
                            photoPicker.launch()
                        }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
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
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        ) {

            // 1. Photo
            item {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(vertical = 8.dp),
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
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.photo_camera_24px),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.vehicle_photo_tap_to_add),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.vehicle_photo_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // 2. Brand dropdown
            item {
                Text(
                    text = stringResource(Res.string.vehicle_brand_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
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
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(
                        value = state.brand,
                        onValueChange = { onAction(RegisterVehicleAction.OnBrandSelected(it)) },
                        label = stringResource(Res.string.vehicle_brand_other),
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        errorMessage = if (state.brandError) " " else null
                    )
                }
                if (state.brandError) {
                    Text(
                        text = stringResource(Res.string.vehicle_brand_label) + " es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // 3. Model
            item {
                AuthTextField(
                    value = state.model,
                    onValueChange = { onAction(RegisterVehicleAction.OnModelChanged(it)) },
                    label = stringResource(Res.string.vehicle_model_label),
                    placeholder = stringResource(Res.string.vehicle_model_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = if (state.modelError) " " else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. Year dropdown
            item {
                Text(
                    text = stringResource(Res.string.vehicle_year_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
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
                Spacer(Modifier.height(16.dp))
            }

            // 5. Color chips
            item {
                Text(
                    text = stringResource(Res.string.vehicle_color_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
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
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorLabels.forEach { (value, label) ->
                        FilterChip(
                            selected = state.color == value,
                            onClick = { onAction(RegisterVehicleAction.OnColorSelected(value)) },
                            label = { Text(label) }
                        )
                    }
                }
                if (state.isCustomColor) {
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(
                        value = state.customColor,
                        onValueChange = { onAction(RegisterVehicleAction.OnCustomColorChanged(it)) },
                        label = stringResource(Res.string.vehicle_color_other),
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        errorMessage = if (state.colorError) " " else null
                    )
                }
                if (state.colorError) {
                    Text(
                        text = stringResource(Res.string.vehicle_color_label) + " es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // 6. Plate
            item {
                AuthTextField(
                    value = state.plate,
                    onValueChange = { onAction(RegisterVehicleAction.OnPlateChanged(it)) },
                    label = stringResource(Res.string.vehicle_plate_label),
                    placeholder = "ABC123",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = ColombianPlateVisualTransformation(),
                    errorMessage = if (state.plateError) stringResource(Res.string.vehicle_plate_error_format) else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // 7. Seat count stepper
            item {
                Text(
                    text = stringResource(Res.string.vehicle_seats_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
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
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 8. Vehicle type (optional)
            item {
                Text(
                    text = stringResource(Res.string.vehicle_type_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val typeEntries = listOf(
                    VehicleType.Sedan to stringResource(Res.string.vehicle_type_sedan),
                    VehicleType.Hatchback to stringResource(Res.string.vehicle_type_hatchback),
                    VehicleType.SUV to stringResource(Res.string.vehicle_type_suv),
                    VehicleType.Pickup to stringResource(Res.string.vehicle_type_pickup),
                    VehicleType.Other to stringResource(Res.string.vehicle_type_other),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    typeEntries.forEach { (type, label) ->
                        FilterChip(
                            selected = state.type == type,
                            onClick = { onAction(RegisterVehicleAction.OnTypeSelected(type)) },
                            label = { Text(label) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 9. Documents (collapsible)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(RegisterVehicleAction.OnToggleDocuments) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.vehicle_docs_section),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (state.showDocuments)
                            stringResource(Res.string.vehicle_docs_hide)
                        else
                            stringResource(Res.string.vehicle_docs_show),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (state.showDocuments) {
                    Text(
                        text = stringResource(Res.string.vehicle_docs_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    // SOAT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.vehicle_soat_label),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { onAction(RegisterVehicleAction.OnShowSoatDatePicker) }) {
                            Text(
                                text = state.soatDate?.toString()
                                    ?: stringResource(Res.string.vehicle_docs_show)
                            )
                        }
                    }
                    // Tecnomecánica
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.vehicle_tecno_label),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { onAction(RegisterVehicleAction.OnShowTecnoDatePicker) }) {
                            Text(
                                text = state.tecnomecanicaDate?.toString()
                                    ?: stringResource(Res.string.vehicle_docs_show)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // General error
            item {
                if (state.generalError != null) {
                    ErrorMessage(message = stringResource(state.generalError.asStringResource()))
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Save button
            item {
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = if (state.mode == RegisterVehicleUiState.Mode.Edit)
                        stringResource(Res.string.vehicle_update_button)
                    else
                        stringResource(Res.string.vehicle_save_button),
                    onClick = { onAction(RegisterVehicleAction.OnSaveClick) },
                    enabled = state.isValid,
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(24.dp))
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
