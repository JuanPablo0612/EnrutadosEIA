package com.juanpablo0612.carpool.presentation.auth.email_verification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel,
    onNavigateToApp: (com.juanpablo0612.carpool.domain.auth.model.User) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EmailVerificationEvent.NavigateToApp -> onNavigateToApp(event.user)
            EmailVerificationEvent.OpenGmail -> { /* platform-handled */ }
        }
    }

    EmailVerificationContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}

@Composable
fun EmailVerificationContent(
    state: EmailVerificationUiState,
    onAction: (EmailVerificationAction) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.email_verification_title),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompactAuthHeader(screenTitle = stringResource(Res.string.email_verification_title))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.email_verification_subtitle, state.obfuscatedEmail),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = stringResource(Res.string.email_verification_open_gmail),
                onClick = { onAction(EmailVerificationAction.OnOpenGmail) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            val resendText = if (state.resendCountdown > 0) {
                stringResource(Res.string.email_verification_resend_countdown, state.resendCountdown)
            } else {
                stringResource(Res.string.email_verification_resend)
            }

            SecondaryButton(
                text = resendText,
                onClick = { onAction(EmailVerificationAction.OnResendEmail) },
                enabled = state.resendCountdown == 0 && !state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            state.error?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
                Spacer(modifier = Modifier.height(8.dp))
            }

            AuthClickableText(
                text = stringResource(Res.string.email_verification_wrong_email),
                onClick = onBackClick
            )
        }
    }
}

@Preview
@Composable
private fun EmailVerificationPreview() {
    CarpoolTheme {
        EmailVerificationContent(
            state = EmailVerificationUiState(
                obfuscatedEmail = "j***@eia.edu.co",
                resendCountdown = 25
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}
