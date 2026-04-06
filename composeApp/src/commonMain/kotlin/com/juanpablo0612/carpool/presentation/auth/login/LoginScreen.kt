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
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateToHome -> onLoginSuccess()
            else -> Unit
        }
    }

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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader(
                title = stringResource(Res.string.login_welcome_back),
                subtitle = stringResource(Res.string.login_subtitle),
                imageRes = Res.drawable.login_image
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoginForm(
                state = state,
                onAction = viewModel::onAction,
                onForgotPasswordClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthDivider(text = stringResource(Res.string.or_separator))

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.dont_have_account_question),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            SecondaryButton(
                text = stringResource(Res.string.signup_button),
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(24.dp))
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

        Spacer(modifier = Modifier.height(16.dp))

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
            AuthClickableText(
                text = stringResource(Res.string.forgot_password),
                onClick = onForgotPasswordClick,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(Res.string.login_button),
            onClick = { onAction(LoginAction.OnLoginClicked) },
            isLoading = state.isLoading
        )
    }
}
