package com.juanpablo0612.carpool.presentation.auth.login

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
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (com.juanpablo0612.carpool.domain.auth.model.User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateAfterAuth -> onLoginSuccess(event.user)
            AuthEvent.NavigateToEmailVerification -> { /* not emitted from login */ }
        }
    }

    LoginContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPasswordClick = onForgotPasswordClick,
        onBackClick = onBackClick
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.login_title),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.screenHorizontalForm)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.lg))

            CompactAuthHeader(screenTitle = stringResource(Res.string.login_welcome_back))

            Spacer(modifier = Modifier.height(Spacing.xxl))

            LoginForm(
                state = state,
                onAction = onAction,
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(Res.string.dont_have_account_question),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            SecondaryButton(
                text = stringResource(Res.string.signup_button),
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = stringResource(Res.string.login_terms_footer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
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
    Column(modifier = modifier.fillMaxWidth()) {
        EmailTextField(
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

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            LinkText(
                text = stringResource(Res.string.forgot_password),
                onClick = onForgotPasswordClick,
                modifier = Modifier.padding(vertical = Spacing.sm)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(Spacing.lg))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.login_button),
            onClick = { onAction(LoginAction.OnLoginClicked) },
            isLoading = state.isLoading
        )
    }
}

@Preview
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
