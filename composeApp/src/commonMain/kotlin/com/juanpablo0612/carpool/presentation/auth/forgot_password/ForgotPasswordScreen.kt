package com.juanpablo0612.carpool.presentation.auth.forgot_password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
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
            AuthEvent.NavigateToLogin -> onBackClick()
            else -> Unit
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
            AuthHeader(
                title = stringResource(Res.string.forgot_password_title),
                subtitle = stringResource(Res.string.forgot_password_subtitle),
                imageRes = Res.drawable.login_image // Reusing login image as placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (state.isSuccess) {
                SuccessMessage(
                    message = stringResource(Res.string.reset_link_sent_success)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SecondaryButton(
                    text = stringResource(Res.string.back_to_login),
                    onClick = onBackClick
                )
            } else {
                ForgotPasswordForm(
                    state = state,
                    onAction = onAction
                )
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
private fun SuccessMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = org.jetbrains.compose.resources.vectorResource(Res.drawable.check_circle_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenPreview() {
    CarpoolTheme {
        ForgotPasswordContent(
            state = ForgotPasswordUiState(),
            onAction = {},
            onBackClick = {}
        )
    }
}
