package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.LinkText
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.already_have_account_question
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.create_account_button
import enrutadoseia.composeapp.generated.resources.error_terms_not_accepted
import enrutadoseia.composeapp.generated.resources.login_link
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_phone_label
import enrutadoseia.composeapp.generated.resources.register_phone_placeholder
import enrutadoseia.composeapp.generated.resources.register_photo_action_camera
import enrutadoseia.composeapp.generated.resources.register_photo_action_gallery
import enrutadoseia.composeapp.generated.resources.register_photo_error
import enrutadoseia.composeapp.generated.resources.register_photo_placeholder
import enrutadoseia.composeapp.generated.resources.register_terms_checkbox
import enrutadoseia.composeapp.generated.resources.terms_and_privacy
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
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showImageSourceSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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

        if (state.photoError) {
            ErrorMessage(message = stringResource(Res.string.register_photo_error))
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PhoneTextField(
            value = state.phone,
            onValueChange = { onAction(RegisterAction.OnPhoneChanged(it)) },
            label = stringResource(Res.string.register_phone_label),
            placeholder = stringResource(Res.string.register_phone_placeholder),
            errorMessage = state.phoneError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            // Done only closes the keyboard: the terms still need to be accepted.
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.hasAcceptedTerms,
                onCheckedChange = { onAction(RegisterAction.OnTermsChanged(it)) }
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = stringResource(Res.string.register_terms_checkbox),
                style = MaterialTheme.typography.bodySmall,
                color = if (state.termsError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }

        if (state.termsError) {
            Text(
                text = stringResource(Res.string.error_terms_not_accepted),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Spacing.lg)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(Spacing.lg))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.create_account_button),
            onClick = { onAction(RegisterAction.OnRegisterClicked) },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            trailingIcon = vectorResource(Res.drawable.arrow_forward_24px)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.already_have_account_question),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            LinkText(
                text = stringResource(Res.string.login_link),
                onClick = onNavigateToLogin
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = stringResource(Res.string.terms_and_privacy),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg)
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
