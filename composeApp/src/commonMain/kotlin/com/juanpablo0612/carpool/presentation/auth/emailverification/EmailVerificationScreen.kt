package com.juanpablo0612.carpool.presentation.auth.emailverification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
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
        // heightIn(min = maxHeight) rather than a bare verticalScroll: inside a scrollable the
        // column is measured with unbounded height, so Arrangement.Center would have no slack and
        // the content would silently jump to the top. This keeps it centred when it fits and
        // scrollable when it doesn't, which is what landscape and large font scales need.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontalForm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompactAuthHeader(screenTitle = stringResource(Res.string.email_verification_title))

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = stringResource(Res.string.email_verification_subtitle, state.obfuscatedEmail),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            PrimaryButton(
                text = stringResource(Res.string.email_verification_open_gmail),
                onClick = { onAction(EmailVerificationAction.OnOpenGmail) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

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

            Spacer(modifier = Modifier.height(Spacing.sm))

            state.error?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            LinkText(
                text = stringResource(Res.string.email_verification_wrong_email),
                onClick = onBackClick
            )
        }
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
