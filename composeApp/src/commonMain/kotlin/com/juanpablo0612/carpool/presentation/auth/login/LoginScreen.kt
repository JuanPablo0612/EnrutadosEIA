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
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.asStringResource
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
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit,
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

@Composable
fun LoginContent(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit,
    canNavigateBack: Boolean = true
) {
    AuthFormLayout(
        topBar = { AuthTopBar(onBackClick = onBackClick, showBackButton = canNavigateBack) },
        footer = {
            // A full-width button rather than a text link: first-time users kept typing their
            // email here without having an account.
            Text(
                text = stringResource(Res.string.dont_have_account_question),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            SecondaryButton(
                text = stringResource(Res.string.signup_button),
                onClick = onNavigateToRegister,
            )
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
            onForgotPasswordClick = onForgotPasswordClick
        )
    }
}

@Composable
private fun LoginForm(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        EiaEmailTextField(
            value = state.email,
            onValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
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
            errorMessage = state.passwordError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = { onAction(LoginAction.OnLoginClicked) }
            )
        )

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(Res.string.forgot_password), style = MaterialTheme.typography.titleSmall)
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(Spacing.sm))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        PrimaryButton(
            text = stringResource(Res.string.login_button),
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
