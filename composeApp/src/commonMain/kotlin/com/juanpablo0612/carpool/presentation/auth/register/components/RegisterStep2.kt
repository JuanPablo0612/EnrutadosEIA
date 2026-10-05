package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_terms_not_accepted
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_phone_label
import enrutadoseia.composeapp.generated.resources.register_photo_action_camera
import enrutadoseia.composeapp.generated.resources.register_photo_action_gallery
import enrutadoseia.composeapp.generated.resources.register_photo_add
import enrutadoseia.composeapp.generated.resources.register_photo_change
import enrutadoseia.composeapp.generated.resources.register_photo_error
import enrutadoseia.composeapp.generated.resources.register_photo_hint
import enrutadoseia.composeapp.generated.resources.register_photo_placeholder
import enrutadoseia.composeapp.generated.resources.register_step_2_subtitle
import enrutadoseia.composeapp.generated.resources.register_step_2_title
import enrutadoseia.composeapp.generated.resources.register_terms_checkbox
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** The profile step: an optional photo, the phone number and the terms. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegisterStep2(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
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

    Column(modifier = modifier.fillMaxWidth()) {
        AuthHeader(
            title = stringResource(Res.string.register_step_2_title),
            subtitle = stringResource(Res.string.register_step_2_subtitle),
            icon = null,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ProfilePhoto(
                photo = state.photoFile,
                onClick = { showImageSourceSheet = true },
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SecondaryButton(
                    text = stringResource(
                        if (state.photoFile != null) Res.string.register_photo_change else Res.string.register_photo_add
                    ),
                    onClick = { showImageSourceSheet = true },
                )
                Text(
                    text = stringResource(Res.string.register_photo_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (state.photoError) {
            Spacer(modifier = Modifier.height(Spacing.md))
            ErrorMessage(message = stringResource(Res.string.register_photo_error))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PhoneNumberFields(
            countryCode = state.phoneCountryCode,
            number = state.phoneNumber,
            onCountryCodeChange = { onAction(RegisterAction.OnPhoneCountryCodeChanged(it)) },
            onNumberChange = { onAction(RegisterAction.OnPhoneNumberChanged(it)) },
            label = stringResource(Res.string.register_phone_label),
            countryCodeError = state.phoneCountryCodeError?.let { stringResource(it.asStringResource()) },
            numberError = state.phoneNumberError?.let { stringResource(it.asStringResource()) },
            imeAction = ImeAction.Done,
            // Done only closes the keyboard: the terms still need to be accepted.
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        TermsCheckbox(
            checked = state.hasAcceptedTerms,
            isError = state.termsError,
            onCheckedChange = { onAction(RegisterAction.OnTermsChanged(it)) },
        )
    }

    if (showImageSourceSheet) {
        ModalBottomSheet(onDismissRequest = { showImageSourceSheet = false }) {
            Column(modifier = Modifier.navigationBarsPadding().padding(bottom = Spacing.lg)) {
                Text(
                    text = stringResource(Res.string.register_photo_placeholder),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm)
                )
                PhotoSourceItem(
                    text = stringResource(Res.string.register_photo_action_camera),
                    onClick = {
                        showImageSourceSheet = false
                        cameraLauncher.launch()
                    },
                )
                PhotoSourceItem(
                    text = stringResource(Res.string.register_photo_action_gallery),
                    onClick = {
                        showImageSourceSheet = false
                        photoPicker.launch()
                    },
                )
            }
        }
    }
}

@Composable
private fun ProfilePhoto(photo: PlatformFile?, onClick: () -> Unit) {
    // A larger touch target for the button beside it. The button carries the label, so the
    // photo's own semantics are cleared rather than announced as a second, unlabelled button.
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        if (photo != null) {
            AsyncImage(
                model = photo,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = vectorResource(Res.drawable.photo_camera_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun PhotoSourceItem(text: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = text, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    )
}

/** The whole row toggles the box, so the tap target is the full width, not just the 48dp box. */
@Composable
private fun TermsCheckbox(checked: Boolean, isError: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.medium,
                )
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
                .padding(end = Spacing.lg, top = Spacing.xs, bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // onCheckedChange = null: the row owns the toggle, so the box doesn't add a second,
            // nested click target announced separately.
            Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.padding(Spacing.md))
            Text(
                text = stringResource(Res.string.register_terms_checkbox),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (isError) {
            Text(
                text = stringResource(Res.string.error_terms_not_accepted),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.lg, top = Spacing.xs),
            )
        }
    }
}
