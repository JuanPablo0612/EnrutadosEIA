package com.juanpablo0612.carpool.presentation.vehicles.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.components.*
import enrutadoseia.composeapp.generated.resources.*
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
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
    var showImageSourceSelector by remember { mutableStateOf(false) }

    val photoPicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { file ->
        if (file != null) {
            onAction(RegisterVehicleAction.OnPhotoSelected(file))
        }
    }

    val cameraLauncher = rememberCameraPickerLauncher { file ->
        if (file != null) {
            onAction(RegisterVehicleAction.OnPhotoSelected(file))
        }
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.register_vehicle_title),
                onBackClick = { onAction(RegisterVehicleAction.OnBackClick) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo picker
            item {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { showImageSourceSelector = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.vehiclePhoto != null) {
                        AsyncImage(
                            model = state.vehiclePhoto,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(vertical = 12.dp),
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
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.photo_camera_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(Res.string.vehicle_add_photo),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (state.error == RegisterVehicleError.PhotoRequired) {
                    Text(
                        text = stringResource(RegisterVehicleError.PhotoRequired.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            // Brand
            item {
                AuthTextField(
                    value = state.brand,
                    onValueChange = { onAction(RegisterVehicleAction.OnBrandChanged(it)) },
                    label = stringResource(Res.string.vehicle_brand_label),
                    placeholder = stringResource(Res.string.vehicle_brand_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = if (state.error == RegisterVehicleError.BrandRequired) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // Model
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
                    errorMessage = if (state.error == RegisterVehicleError.ModelRequired) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // License plate
            item {
                AuthTextField(
                    value = state.licensePlate,
                    onValueChange = { onAction(RegisterVehicleAction.OnLicensePlateChanged(it)) },
                    label = stringResource(Res.string.vehicle_license_plate_label),
                    placeholder = stringResource(Res.string.vehicle_license_plate_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = if (state.error == RegisterVehicleError.LicensePlateRequired) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // Color
            item {
                AuthTextField(
                    value = state.color,
                    onValueChange = { onAction(RegisterVehicleAction.OnColorChanged(it)) },
                    label = stringResource(Res.string.vehicle_color_label),
                    placeholder = stringResource(Res.string.vehicle_color_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = if (state.error == RegisterVehicleError.ColorRequired) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // Year
            item {
                AuthTextField(
                    value = state.year,
                    onValueChange = { onAction(RegisterVehicleAction.OnYearChanged(it)) },
                    label = stringResource(Res.string.vehicle_year_label),
                    placeholder = stringResource(Res.string.vehicle_year_placeholder),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = if (state.error == RegisterVehicleError.YearRequired ||
                        state.error == RegisterVehicleError.YearInvalid
                    ) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(16.dp))
            }

            // Seats available
            item {
                AuthTextField(
                    value = state.seatsAvailable,
                    onValueChange = { onAction(RegisterVehicleAction.OnSeatsChanged(it)) },
                    label = stringResource(Res.string.vehicle_seats_label),
                    placeholder = stringResource(Res.string.vehicle_seats_placeholder),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    errorMessage = if (state.error == RegisterVehicleError.SeatsRequired ||
                        state.error == RegisterVehicleError.SeatsInvalid
                    ) {
                        stringResource(state.error.asStringResource())
                    } else null
                )
                Spacer(Modifier.height(24.dp))
            }

            // Generic error
            item {
                if (state.error == RegisterVehicleError.UserNotAuthenticated ||
                    state.error == RegisterVehicleError.Unknown
                ) {
                    ErrorMessage(message = stringResource(state.error.asStringResource()))
                    Spacer(Modifier.height(24.dp))
                }
            }

            // Save button
            item {
                PrimaryButton(
                    text = stringResource(Res.string.register_vehicle_button),
                    onClick = { onAction(RegisterVehicleAction.OnSaveClick) },
                    isLoading = state.isLoading
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showImageSourceSelector) {
        ModalBottomSheet(
            onDismissRequest = { showImageSourceSelector = false },
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
                    text = stringResource(Res.string.vehicle_select_photo_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showImageSourceSelector = false
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
                        text = stringResource(Res.string.vehicle_select_camera),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showImageSourceSelector = false
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
                        text = stringResource(Res.string.vehicle_select_gallery),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RegisterVehicleScreenPreview() {
    CarpoolTheme {
        RegisterVehicleContent(
            state = RegisterVehicleUiState(
                brand = "Toyota",
                model = "Corolla",
                licensePlate = "ABC-123",
                color = "Blanco",
                year = "2024",
                seatsAvailable = "4"
            ),
            onAction = {}
        )
    }
}