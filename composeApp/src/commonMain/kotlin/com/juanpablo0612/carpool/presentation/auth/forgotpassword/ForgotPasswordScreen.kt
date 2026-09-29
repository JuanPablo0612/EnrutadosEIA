package com.juanpablo0612.carpool.presentation.auth.forgotpassword

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.AuthFormLayout
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.AuthNote
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EiaEmailTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.auth_spam_hint
import enrutadoseia.composeapp.generated.resources.back_to_login
import enrutadoseia.composeapp.generated.resources.email_label
import enrutadoseia.composeapp.generated.resources.email_placeholder
import enrutadoseia.composeapp.generated.resources.forgot_password_resend_button
import enrutadoseia.composeapp.generated.resources.forgot_password_resend_countdown
import enrutadoseia.composeapp.generated.resources.forgot_password_subtitle
import enrutadoseia.composeapp.generated.resources.forgot_password_success_subtitle
import enrutadoseia.composeapp.generated.resources.forgot_password_success_title
import enrutadoseia.composeapp.generated.resources.forgot_password_title
import enrutadoseia.composeapp.generated.resources.lock_24px
import enrutadoseia.composeapp.generated.resources.mail_24px
import enrutadoseia.composeapp.generated.resources.send_reset_link
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
    if (state.isSuccess) {
        ForgotPasswordSent(state = state, onAction = onAction, onBackClick = onBackClick)
    } else {
        ForgotPasswordForm(state = state, onAction = onAction, onBackClick = onBackClick)
    }
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordUiState,
    onAction: (ForgotPasswordAction) -> Unit,
    onBackClick: () -> Unit,
) {
    AuthFormLayout(
        topBar = { AuthTopBar(onBackClick = onBackClick) },
        footer = {
            PrimaryButton(
                text = stringResource(Res.string.send_reset_link),
                onClick = { onAction(ForgotPasswordAction.OnSendResetLink) },
                isLoading = state.isLoading
            )
        },
    ) {
        AuthHeader(
            title = stringResource(Res.string.forgot_password_title),
            subtitle = stringResource(Res.string.forgot_password_subtitle),
            icon = vectorResource(Res.drawable.lock_24px),
            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        EiaEmailTextField(
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
            Spacer(modifier = Modifier.height(Spacing.lg))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }
    }
}

@Composable
private fun ForgotPasswordSent(
    state: ForgotPasswordUiState,
    onAction: (ForgotPasswordAction) -> Unit,
    onBackClick: () -> Unit,
) {
    AuthFormLayout(
        topBar = { AuthTopBar(onBackClick = onBackClick) },
        footer = {
            PrimaryButton(
                text = stringResource(Res.string.back_to_login),
                onClick = onBackClick
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            SecondaryButton(
                text = if (state.resendCountdown > 0) {
                    stringResource(Res.string.forgot_password_resend_countdown, state.resendCountdown)
                } else {
                    stringResource(Res.string.forgot_password_resend_button)
                },
                onClick = { onAction(ForgotPasswordAction.OnResendLink) },
                enabled = state.resendCountdown == 0 && !state.isLoading
            )
        },
    ) {
        AuthHeader(
            title = stringResource(Res.string.forgot_password_success_title),
            subtitle = stringResource(Res.string.forgot_password_success_subtitle, state.obfuscatedEmail),
            icon = vectorResource(Res.drawable.mail_24px),
            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        AuthNote(text = stringResource(Res.string.auth_spam_hint))

        state.error?.let {
            Spacer(modifier = Modifier.height(Spacing.lg))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }
    }
}

@Preview
@Composable
private fun ForgotPasswordFormPreview() {
    CarpoolTheme {
        ForgotPasswordContent(
            state = ForgotPasswordUiState(email = "juan.perez"),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordSentPreview() {
    CarpoolTheme {
        ForgotPasswordContent(
            state = ForgotPasswordUiState(isSuccess = true, obfuscatedEmail = "j***@eia.edu.co", resendCountdown = 24),
            onAction = {},
            onBackClick = {}
        )
    }
}
