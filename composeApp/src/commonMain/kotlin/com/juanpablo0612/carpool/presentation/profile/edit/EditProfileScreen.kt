package com.juanpablo0612.carpool.presentation.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.components.NameTextField
import com.juanpablo0612.carpool.presentation.auth.register.components.PhoneTextField
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.full_name_placeholder
import enrutadoseia.composeapp.generated.resources.discard_changes_body
import enrutadoseia.composeapp.generated.resources.discard_changes_confirm
import enrutadoseia.composeapp.generated.resources.discard_changes_title
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_counter
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_label
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_placeholder
import enrutadoseia.composeapp.generated.resources.edit_profile_name_label
import enrutadoseia.composeapp.generated.resources.edit_profile_phone_label
import enrutadoseia.composeapp.generated.resources.edit_profile_phone_placeholder
import enrutadoseia.composeapp.generated.resources.edit_profile_photo_error
import enrutadoseia.composeapp.generated.resources.edit_profile_save_button
import enrutadoseia.composeapp.generated.resources.edit_profile_title
import enrutadoseia.composeapp.generated.resources.notice_profile_saved
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.register_photo_action_camera
import enrutadoseia.composeapp.generated.resources.register_photo_action_gallery
import enrutadoseia.composeapp.generated.resources.register_photo_placeholder
import enrutadoseia.composeapp.generated.resources.vehicle_change_photo
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EditProfileEvent.SaveSuccess -> onSaved()
            EditProfileEvent.NavigateBack -> onBackClick()
        }
    }

    BackHandler {
        viewModel.onAction(EditProfileAction.OnBackClick)
    }

    EditProfileContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    state: EditProfileUiState,
    onAction: (EditProfileAction) -> Unit
) {
    var showImageSourceSheet by remember { mutableStateOf(false) }

    val photoPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        onAction(EditProfileAction.OnPhotoSelected(file))
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        onAction(EditProfileAction.OnPhotoSelected(file))
    }

    if (state.showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(EditProfileAction.OnConfirmDiscard) },
            onDismiss = { onAction(EditProfileAction.OnDismissDiscardConfirm) },
            isDestructive = true
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.edit_profile_title),
                onBack = { onAction(EditProfileAction.OnBackClick) },
            )
        }
    ) { padding ->
        if (state.isLoading) {
            DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .padding(horizontal = Spacing.screenHorizontal)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Spacer(Modifier.height(Spacing.sm))

                Box(
                    modifier = Modifier
                        // Component-intrinsic avatar diameter, not a spacing step.
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showImageSourceSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    val photoModel = state.photoFile ?: state.existingPhotoUrl
                    if (photoModel != null) {
                        AsyncImage(
                            model = photoModel,
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

                TextButton(onClick = { showImageSourceSheet = true }) {
                    Text(
                        text = stringResource(
                            if (state.photoFile != null || state.existingPhotoUrl != null) {
                                Res.string.vehicle_change_photo
                            } else {
                                Res.string.register_photo_placeholder
                            }
                        )
                    )
                }

                if (state.photoError) {
                    ErrorMessage(message = stringResource(Res.string.edit_profile_photo_error))
                }

                NameTextField(
                    value = state.name,
                    onValueChange = { onAction(EditProfileAction.OnNameChange(it)) },
                    label = stringResource(Res.string.edit_profile_name_label),
                    placeholder = stringResource(Res.string.full_name_placeholder),
                    errorMessage = state.nameError?.asStringResource()?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                PhoneTextField(
                    value = state.phone,
                    onValueChange = { onAction(EditProfileAction.OnPhoneChange(it)) },
                    label = stringResource(Res.string.edit_profile_phone_label),
                    placeholder = stringResource(Res.string.edit_profile_phone_placeholder),
                    errorMessage = state.phoneError?.asStringResource()?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                CarpoolTextField(
                    value = state.bio,
                    onValueChange = { onAction(EditProfileAction.OnBioChange(it)) },
                    label = stringResource(Res.string.edit_profile_bio_label),
                    placeholder = stringResource(Res.string.edit_profile_bio_placeholder),
                    errorMessage = state.bioError?.let { stringResource(it.asStringResource()) },
                    supportingText = {
                        Text(
                            text = stringResource(Res.string.edit_profile_bio_counter, state.bio.length),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                state.error?.let {
                    ErrorMessage(message = stringResource(it.asStringResource()))
                }

                if (state.isSaved) {
                    SuccessMessage(message = stringResource(Res.string.notice_profile_saved))
                }

                PrimaryButton(
                    text = stringResource(Res.string.edit_profile_save_button),
                    onClick = { onAction(EditProfileAction.OnSaveClick) },
                    enabled = !state.isSaving && !state.isSaved,
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(Spacing.lg))
            }
        }
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
