package com.juanpablo0612.carpool.presentation.auth.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.login.components.LoginHelpCard
import com.juanpablo0612.carpool.presentation.ui.components.AuthFormLayout
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EiaEmailTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PasswordTextField
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.dont_have_account_question
import enrutadoseia.composeapp.generated.resources.email_label
import enrutadoseia.composeapp.generated.resources.email_placeholder
import enrutadoseia.composeapp.generated.resources.forgot_password
import enrutadoseia.composeapp.generated.resources.login_button
import enrutadoseia.composeapp.generated.resources.login_credentials_rejected
import enrutadoseia.composeapp.generated.resources.login_retry_button
import enrutadoseia.composeapp.generated.resources.login_subtitle
import enrutadoseia.composeapp.generated.resources.login_title
import enrutadoseia.composeapp.generated.resources.password_label
import enrutadoseia.composeapp.generated.resources.password_placeholder
import enrutadoseia.composeapp.generated.resources.signup_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: (email: String?) -> Unit,
    onForgotPasswordClick: (email: String?) -> Unit,
    onNavigateToEmailVerification: () -> Unit,
    onBackClick: () -> Unit,
    canNavigateBack: Boolean = true
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateAfterAuth -> onLoginSuccess(event.user)
            AuthEvent.NavigateToEmailVerification -> onNavigateToEmailVerification()
        }
    }

    LoginContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPasswordClick = onForgotPasswordClick,
        onBackClick = onBackClick,
        canNavigateBack = canNavigateBack
    )
}

/**
 * The sign-in form. Both ways out (sign-up, password reset) carry the typed address along so the
 * next form starts filled in.
 */
@Composable
fun LoginContent(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegister: (email: String?) -> Unit,
    onForgotPasswordClick: (email: String?) -> Unit,
    onBackClick: () -> Unit,
    canNavigateBack: Boolean = true
) {
    AuthFormLayout(
        topBar = { AuthTopBar(onBackClick = onBackClick, showBackButton = canNavigateBack) },
        footer = {
            // After a rejected sign-in the help card already offers sign-up; no need to repeat it.
            if (!state.isCredentialsRejected) {
                // A full-width button rather than a text link: first-time users kept typing
                // their email here without having an account.
                Text(
                    text = stringResource(Res.string.dont_have_account_question),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                SecondaryButton(
                    text = stringResource(Res.string.signup_button),
                    onClick = { onNavigateToRegister(state.typedEmail) },
                )
            }
        },
    ) {
        AuthHeader(
            title = stringResource(Res.string.login_title),
            icon = null,
            subtitle = stringResource(Res.string.login_subtitle),
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        LoginForm(
            state = state,
            onAction = onAction,
            onForgotPasswordClick = { onForgotPasswordClick(state.typedEmail) }
        )

        if (state.isCredentialsRejected) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            LoginHelpCard(
                onForgotPasswordClick = { onForgotPasswordClick(state.typedEmail) },
                onCreateAccountClick = { onNavigateToRegister(state.typedEmail) },
            )
        }
    }
}

@Composable
private fun LoginForm(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRejected = state.isCredentialsRejected
    Column(modifier = modifier.fillMaxWidth()) {
        EiaEmailTextField(
            value = state.email,
            onValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
            isError = state.emailError != null || isRejected,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        PasswordTextField(
            value = state.password,
            onValueChange = { onAction(LoginAction.OnPasswordChanged(it)) },
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder),
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { onAction(LoginAction.OnTogglePasswordVisibility) },
            // One neutral message under both fields: it may be the password or a missing
            // account, and saying which would also reveal whether the address is registered.
            errorMessage = state.passwordError?.asStringResource()?.let { stringResource(it) }
                ?: if (isRejected) stringResource(Res.string.login_credentials_rejected) else null,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = { onAction(LoginAction.OnLoginClicked) }
            )
        )

        if (!isRejected) {
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(Res.string.forgot_password), style = MaterialTheme.typography.titleSmall)
            }
        }

        // Other failures (network, unknown) keep the generic message, without the help card.
        state.error?.takeUnless { isRejected }?.let {
            Spacer(modifier = Modifier.height(Spacing.sm))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        PrimaryButton(
            text = stringResource(if (isRejected) Res.string.login_retry_button else Res.string.login_button),
            onClick = { onAction(LoginAction.OnLoginClicked) },
            isLoading = state.isLoading
        )
    }
}

@ScreenPreviews
@Composable
private fun LoginScreenPreview() {
    CarpoolTheme {
        LoginContent(
            state = LoginUiState(),
            onAction = {},
            onNavigateToRegister = {},
            onForgotPasswordClick = {},
            onBackClick = {}
        )
    }
}

@ScreenPreviews
@Composable
private fun LoginRejectedPreview() {
    CarpoolTheme {
        LoginContent(
            state = LoginUiState(
                email = "juan.perez",
                password = "contrasena123",
                error = AuthError.InvalidCredentials,
            ),
            onAction = {},
            onNavigateToRegister = {},
            onForgotPasswordClick = {},
            onBackClick = {}
        )
    }
}
