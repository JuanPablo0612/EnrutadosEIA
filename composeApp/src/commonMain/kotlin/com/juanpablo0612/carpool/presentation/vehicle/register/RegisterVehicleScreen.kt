package com.juanpablo0612.carpool.presentation.vehicle.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleBrandSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleColorSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleModelSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehiclePhotoSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehiclePlateSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleSeatsSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleTypeSection
import com.juanpablo0612.carpool.presentation.vehicle.register.components.VehicleYearSection
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.edit_vehicle_title
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_vehicle_title
import enrutadoseia.composeapp.generated.resources.vehicle_photo_choose_gallery
import enrutadoseia.composeapp.generated.resources.vehicle_photo_section
import enrutadoseia.composeapp.generated.resources.vehicle_photo_take_photo
import enrutadoseia.composeapp.generated.resources.vehicle_save_button
import enrutadoseia.composeapp.generated.resources.vehicle_update_button
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
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

@OptIn(ExperimentalMaterial3Api::class)
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
                VehiclePhotoSection(
                    photoFile = state.photoFile,
                    existingPhotoUrl = state.existingPhotoUrl,
                    onShowPhotoSheet = { onAction(RegisterVehicleAction.OnShowPhotoSheet) }
                )
            }

            // 2. Brand dropdown
            item {
                VehicleBrandSection(
                    showBrandDropdown = state.showBrandDropdown,
                    isCustomBrand = state.isCustomBrand,
                    brand = state.brand,
                    brandError = state.brandError,
                    onToggleBrandDropdown = { onAction(RegisterVehicleAction.OnToggleBrandDropdown) },
                    onBrandSelected = { onAction(RegisterVehicleAction.OnBrandSelected(it)) }
                )
            }

            // 3. Model
            item {
                VehicleModelSection(
                    model = state.model,
                    modelError = state.modelError,
                    onModelChanged = { onAction(RegisterVehicleAction.OnModelChanged(it)) }
                )
            }

            // 4. Year dropdown
            item {
                VehicleYearSection(
                    showYearDropdown = state.showYearDropdown,
                    year = state.year,
                    onToggleYearDropdown = { onAction(RegisterVehicleAction.OnToggleYearDropdown) },
                    onYearSelected = { onAction(RegisterVehicleAction.OnYearSelected(it)) }
                )
            }

            // 5. Color chips
            item {
                VehicleColorSection(
                    color = state.color,
                    isCustomColor = state.isCustomColor,
                    customColor = state.customColor,
                    colorError = state.colorError,
                    onColorSelected = { onAction(RegisterVehicleAction.OnColorSelected(it)) },
                    onCustomColorChanged = { onAction(RegisterVehicleAction.OnCustomColorChanged(it)) }
                )
            }

            // 6. Plate
            item {
                VehiclePlateSection(
                    plate = state.plate,
                    plateError = state.plateError,
                    onPlateChanged = { onAction(RegisterVehicleAction.OnPlateChanged(it)) }
                )
            }

            // 7. Seat count stepper
            item {
                VehicleSeatsSection(
                    seatCount = state.seatCount,
                    onSeatCountChanged = { onAction(RegisterVehicleAction.OnSeatCountChanged(it)) }
                )
            }

            // 8. Vehicle type (optional)
            item {
                VehicleTypeSection(
                    type = state.type,
                    onTypeSelected = { onAction(RegisterVehicleAction.OnTypeSelected(it)) }
                )
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
