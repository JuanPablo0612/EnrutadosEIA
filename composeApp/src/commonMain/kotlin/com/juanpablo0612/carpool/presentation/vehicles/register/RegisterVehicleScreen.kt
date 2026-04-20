package com.juanpablo0612.carpool.presentation.vehicles.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.*
import io.github.vinceglb.filekit.dialogs.FileKitType
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
    val photoPicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { file ->
        if (file != null) {
            onAction(RegisterVehicleAction.OnPhotoSelected(file))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.register_vehicle_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(RegisterVehicleAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Photo picker
            item {
                Text(
                    text = stringResource(Res.string.vehicle_photo_section),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { photoPicker.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.vehiclePhoto != null) {
                        AsyncImage(
                            model = state.vehiclePhoto,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // "Tap to change" overlay label at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.vehicle_change_photo),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.add_24px),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.vehicle_add_photo),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (state.error == RegisterVehicleError.PhotoRequired) {
                    Text(
                        text = stringResource(RegisterVehicleError.PhotoRequired.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            // Brand
            item {
                OutlinedTextField(
                    value = state.brand,
                    onValueChange = { onAction(RegisterVehicleAction.OnBrandChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_brand_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_brand_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.error == RegisterVehicleError.BrandRequired,
                    supportingText = if (state.error == RegisterVehicleError.BrandRequired) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Model
            item {
                OutlinedTextField(
                    value = state.model,
                    onValueChange = { onAction(RegisterVehicleAction.OnModelChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_model_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_model_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.error == RegisterVehicleError.ModelRequired,
                    supportingText = if (state.error == RegisterVehicleError.ModelRequired) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // License plate
            item {
                OutlinedTextField(
                    value = state.licensePlate,
                    onValueChange = { onAction(RegisterVehicleAction.OnLicensePlateChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_license_plate_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_license_plate_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Unspecified,
                        imeAction = ImeAction.Next,
                        platformImeOptions = null,
                        showKeyboardOnFocus = null,
                        hintLocales = null
                    ),
                    isError = state.error == RegisterVehicleError.LicensePlateRequired,
                    supportingText = if (state.error == RegisterVehicleError.LicensePlateRequired) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Color
            item {
                OutlinedTextField(
                    value = state.color,
                    onValueChange = { onAction(RegisterVehicleAction.OnColorChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_color_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_color_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.error == RegisterVehicleError.ColorRequired,
                    supportingText = if (state.error == RegisterVehicleError.ColorRequired) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Year
            item {
                OutlinedTextField(
                    value = state.year,
                    onValueChange = { onAction(RegisterVehicleAction.OnYearChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_year_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_year_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.error == RegisterVehicleError.YearRequired ||
                            state.error == RegisterVehicleError.YearInvalid,
                    supportingText = if (state.error == RegisterVehicleError.YearRequired ||
                        state.error == RegisterVehicleError.YearInvalid
                    ) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Seats available
            item {
                OutlinedTextField(
                    value = state.seatsAvailable,
                    onValueChange = { onAction(RegisterVehicleAction.OnSeatsChanged(it)) },
                    label = { Text(stringResource(Res.string.vehicle_seats_label)) },
                    placeholder = { Text(stringResource(Res.string.vehicle_seats_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.error == RegisterVehicleError.SeatsRequired ||
                            state.error == RegisterVehicleError.SeatsInvalid,
                    supportingText = if (state.error == RegisterVehicleError.SeatsRequired ||
                        state.error == RegisterVehicleError.SeatsInvalid
                    ) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Generic error (Unknown / UserNotAuthenticated)
            item {
                if (state.error == RegisterVehicleError.UserNotAuthenticated ||
                    state.error == RegisterVehicleError.Unknown
                ) {
                    Text(
                        text = stringResource(state.error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Save button
            item {
                Button(
                    onClick = { onAction(RegisterVehicleAction.OnSaveClick) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.register_vehicle_button))
                    }
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
