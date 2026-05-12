package com.juanpablo0612.carpool.presentation.auth.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (com.juanpablo0612.carpool.domain.auth.model.User) -> Unit,
    onNavigateToEmailVerification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateAfterAuth -> onRegisterSuccess(event.user)
            AuthEvent.NavigateToEmailVerification -> onNavigateToEmailVerification()
        }
    }

    RegisterContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToLogin = onNavigateToLogin,
        onBackClick = onBackClick
    )
}

@Composable
fun RegisterContent(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val stepTitle = when (state.currentStep) {
        1 -> stringResource(Res.string.register_step_1_title)
        2 -> stringResource(Res.string.register_step_2_title)
        else -> stringResource(Res.string.register_step_3_title)
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.register_title),
                onBackClick = {
                    if (state.currentStep > 1) onAction(RegisterAction.OnPreviousStep)
                    else onBackClick()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            CompactAuthHeader(screenTitle = stepTitle)

            Spacer(modifier = Modifier.height(12.dp))

            StepIndicator(
                current = state.currentStep,
                total = 3,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = stringResource(Res.string.register_step_indicator, state.currentStep, 3),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(targetState = state.currentStep) { step ->
                when (step) {
                    1 -> RegisterStep1(state = state, onAction = onAction)
                    2 -> RegisterStep2(state = state, onAction = onAction)
                    else -> RegisterStep3(
                        state = state,
                        onAction = onAction,
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegisterStep1(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        NameTextField(
            value = state.fullName,
            onValueChange = { onAction(RegisterAction.OnFullNameChanged(it)) },
            label = stringResource(Res.string.full_name_label),
            placeholder = stringResource(Res.string.full_name_placeholder),
            errorMessage = state.fullNameError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        EmailTextField(
            value = state.email,
            onValueChange = { onAction(RegisterAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = state.password,
            onValueChange = { onAction(RegisterAction.OnPasswordChanged(it)) },
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder),
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { onAction(RegisterAction.OnTogglePasswordVisibility) },
            errorMessage = state.passwordError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        if (state.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrengthIndicator(password = state.password)
        }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = state.confirmPassword,
            onValueChange = { onAction(RegisterAction.OnConfirmPasswordChanged(it)) },
            label = stringResource(Res.string.confirm_password_label),
            placeholder = stringResource(Res.string.password_placeholder),
            isPasswordVisible = state.isConfirmPasswordVisible,
            onTogglePasswordVisibility = { onAction(RegisterAction.OnToggleConfirmPasswordVisibility) },
            errorMessage = state.confirmPasswordError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onAction(RegisterAction.OnNextStep) })
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(Res.string.register_continue_button),
            onClick = { onAction(RegisterAction.OnNextStep) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterStep2(
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
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { showImageSourceSheet = true }) {
            Text(
                text = stringResource(
                    if (state.photoFile != null) Res.string.vehicle_change_photo
                    else Res.string.register_photo_placeholder
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PhoneTextField(
            value = state.phone,
            onValueChange = { onAction(RegisterAction.OnPhoneChanged(it)) },
            label = stringResource(Res.string.register_phone_label),
            placeholder = stringResource(Res.string.register_phone_placeholder),
            errorMessage = state.phoneError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onAction(RegisterAction.OnNextStep) })
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(Res.string.register_continue_button),
            onClick = { onAction(RegisterAction.OnNextStep) }
        )
    }

    if (showImageSourceSheet) {
        ModalBottomSheet(onDismissRequest = { showImageSourceSheet = false }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(Res.string.register_photo_placeholder),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RegisterStep3(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        RoleSelectionSection(
            isPassenger = state.isPassenger,
            isDriver = state.isDriver,
            onPassengerToggle = { onAction(RegisterAction.OnPassengerChanged(it)) },
            onDriverToggle = { onAction(RegisterAction.OnDriverChanged(it)) },
            errorMessage = state.roleError?.asStringResource()?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.hasAcceptedTerms,
                onCheckedChange = { onAction(RegisterAction.OnTermsChanged(it)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
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
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(Res.string.create_account_button),
            onClick = { onAction(RegisterAction.OnRegisterClicked) },
            isLoading = state.isLoading,
            trailingIcon = vectorResource(Res.drawable.arrow_forward_24px)
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            Spacer(modifier = Modifier.width(4.dp))
            AuthClickableText(
                text = stringResource(Res.string.login_link),
                onClick = onNavigateToLogin
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.terms_and_privacy),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun RoleSelectionSection(
    isPassenger: Boolean,
    isDriver: Boolean,
    onPassengerToggle: (Boolean) -> Unit,
    onDriverToggle: (Boolean) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.user_type_prompt),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleSelectionCard(
                title = stringResource(Res.string.passenger_option),
                isSelected = isPassenger,
                onClick = { onPassengerToggle(!isPassenger) },
                modifier = Modifier.weight(1f)
            )
            RoleSelectionCard(
                title = stringResource(Res.string.driver_option),
                isSelected = isDriver,
                onClick = { onDriverToggle(!isDriver) },
                modifier = Modifier.weight(1f)
            )
        }
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            ErrorMessage(message = errorMessage)
        }
    }
}

@Preview
@Composable
private fun RegisterStep1Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(currentStep = 1),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun RegisterStep3Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(currentStep = 3),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}
