package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_continue_button
import enrutadoseia.composeapp.generated.resources.register_phone_label
import enrutadoseia.composeapp.generated.resources.register_phone_placeholder
import enrutadoseia.composeapp.generated.resources.register_photo_action_camera
import enrutadoseia.composeapp.generated.resources.register_photo_action_gallery
import enrutadoseia.composeapp.generated.resources.register_photo_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_change_photo
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegisterStep2(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showImageSourceSheet by remember { mutableStateOf(false) }

    val photoPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        onAction(RegisterAction.OnPhotoSelected(file))
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        onAction(RegisterAction.OnPhotoSelected(file))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                // Component-intrinsic avatar diameter, not a spacing step.
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showImageSourceSheet = true },
            contentAlignment = Alignment.Center
        ) {
            if (state.photoFile != null) {
                AsyncImage(
                    model = state.photoFile,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = vectorResource(Res.drawable.photo_camera_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Component-intrinsic icon size, not a spacing step.
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        TextButton(onClick = { showImageSourceSheet = true }) {
            Text(
                text = stringResource(
                    if (state.photoFile != null) Res.string.vehicle_change_photo
                    else Res.string.register_photo_placeholder
                )
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PhoneTextField(
            value = state.phone,
            onValueChange = { onAction(RegisterAction.OnPhoneChanged(it)) },
            label = stringResource(Res.string.register_phone_label),
            placeholder = stringResource(Res.string.register_phone_placeholder),
            errorMessage = state.phoneError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onAction(RegisterAction.OnNextStep) })
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.register_continue_button),
            onClick = { onAction(RegisterAction.OnNextStep) }
        )
    }

    if (showImageSourceSheet) {
        ModalBottomSheet(onDismissRequest = { showImageSourceSheet = false }) {
            Column(modifier = Modifier.padding(horizontal = Spacing.screenHorizontalForm, vertical = Spacing.lg)) {
                Text(
                    text = stringResource(Res.string.register_photo_placeholder),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
                TextButton(
                    onClick = {
                        cameraLauncher.launch()
                        showImageSourceSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.register_photo_action_camera),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TextButton(
                    onClick = {
                        photoPicker.launch()
                        showImageSourceSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.register_photo_action_gallery),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}
