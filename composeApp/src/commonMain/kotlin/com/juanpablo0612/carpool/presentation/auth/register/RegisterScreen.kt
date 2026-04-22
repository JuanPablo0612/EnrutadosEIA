package com.juanpablo0612.carpool.presentation.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateToHome -> onRegisterSuccess()
            else -> Unit
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
    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.register_title),
                onBackClick = onBackClick
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
            AuthHeader(
                title = stringResource(Res.string.register_join_title),
                subtitle = stringResource(Res.string.register_subtitle),
                imageRes = Res.drawable.register_image
            )

            Spacer(modifier = Modifier.height(24.dp))

            RegisterForm(
                state = state,
                onAction = onAction
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

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.terms_and_privacy),
                style = MaterialTheme.typography.labelSmall.copy(lineHeight = 16.sp),
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegisterForm(
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
            keyboardActions = KeyboardActions(
                onDone = { onAction(RegisterAction.OnRegisterClicked) }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        RoleSelectionSection(
            isPassenger = state.isPassenger,
            isDriver = state.isDriver,
            onPassengerToggle = { onAction(RegisterAction.OnPassengerChanged(it)) },
            onDriverToggle = { onAction(RegisterAction.OnDriverChanged(it)) }
        )

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
    }
}

@Composable
private fun RoleSelectionSection(
    isPassenger: Boolean,
    isDriver: Boolean,
    onPassengerToggle: (Boolean) -> Unit,
    onDriverToggle: (Boolean) -> Unit,
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
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}
