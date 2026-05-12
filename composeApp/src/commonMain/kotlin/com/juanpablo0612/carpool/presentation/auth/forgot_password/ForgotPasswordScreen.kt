package com.juanpablo0612.carpool.presentation.auth.forgot_password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ForgotPasswordEvent.OpenGmail -> { /* handled by platform-level intent launcher */ }
        }
    }

    ForgotPasswordContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}

@Composable
fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onAction: (ForgotPasswordAction) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.forgot_password_title),
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
            Spacer(modifier = Modifier.height(16.dp))

            CompactAuthHeader(screenTitle = stringResource(Res.string.forgot_password_title))

            Spacer(modifier = Modifier.height(32.dp))

            if (state.isSuccess) {
                ForgotPasswordSuccess(
                    obfuscatedEmail = state.obfuscatedEmail,
                    resendCountdown = state.resendCountdown,
                    onResend = { onAction(ForgotPasswordAction.OnResendLink) },
                    onOpenGmail = { onAction(ForgotPasswordAction.OnOpenGmail) },
                    onBack = onBackClick
                )
            } else {
                ForgotPasswordForm(state = state, onAction = onAction)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordUiState,
    onAction: (ForgotPasswordAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmailTextField(
            value = state.email,
            onValueChange = { onAction(ForgotPasswordAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = { onAction(ForgotPasswordAction.OnSendResetLink) }
            )
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(Res.string.send_reset_link),
            onClick = { onAction(ForgotPasswordAction.OnSendResetLink) },
            isLoading = state.isLoading
        )
    }
}

@Composable
private fun ForgotPasswordSuccess(
    obfuscatedEmail: String,
    resendCountdown: Int,
    onResend: () -> Unit,
    onOpenGmail: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.forgot_password_success_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(Res.string.forgot_password_success_subtitle, obfuscatedEmail),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = stringResource(Res.string.forgot_password_open_gmail),
            onClick = onOpenGmail
        )

        val resendText = if (resendCountdown > 0) {
            stringResource(Res.string.forgot_password_resend_countdown, resendCountdown)
        } else {
            stringResource(Res.string.forgot_password_resend_button)
        }

        SecondaryButton(
            text = resendText,
            onClick = onResend,
            enabled = resendCountdown == 0
        )

        AuthClickableText(
            text = stringResource(Res.string.back_to_login),
            onClick = onBack
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordFormPreview() {
    CarpoolTheme {
        ForgotPasswordContent(
            state = ForgotPasswordUiState(),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordSuccessPreview() {
    CarpoolTheme {
        ForgotPasswordContent(
            state = ForgotPasswordUiState(
                isSuccess = true,
                obfuscatedEmail = "j***@eia.edu.co",
                resendCountdown = 25
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}
