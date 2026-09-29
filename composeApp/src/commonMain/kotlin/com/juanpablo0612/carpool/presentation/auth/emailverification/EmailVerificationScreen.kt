package com.juanpablo0612.carpool.presentation.auth.emailverification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.AuthFormLayout
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.AuthNote
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.auth_spam_hint
import enrutadoseia.composeapp.generated.resources.email_verification_check_button
import enrutadoseia.composeapp.generated.resources.email_verification_resend
import enrutadoseia.composeapp.generated.resources.email_verification_resend_countdown
import enrutadoseia.composeapp.generated.resources.email_verification_step_1
import enrutadoseia.composeapp.generated.resources.email_verification_step_2
import enrutadoseia.composeapp.generated.resources.email_verification_step_3
import enrutadoseia.composeapp.generated.resources.email_verification_still_unverified
import enrutadoseia.composeapp.generated.resources.email_verification_subtitle
import enrutadoseia.composeapp.generated.resources.email_verification_title
import enrutadoseia.composeapp.generated.resources.email_verification_wrong_email
import enrutadoseia.composeapp.generated.resources.mail_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel,
    onNavigateToApp: (User) -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    // Most users verify from their mail app or a browser and then return here, so resuming is
    // the moment to look; this is what replaces polling.
    LifecycleResumeEffect(viewModel) {
        viewModel.onAction(EmailVerificationAction.OnScreenResumed)
        onPauseOrDispose { }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EmailVerificationEvent.NavigateToApp -> onNavigateToApp(event.user)
            EmailVerificationEvent.NavigateToSignUp -> onNavigateToSignUp()
        }
    }

    EmailVerificationContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun EmailVerificationContent(
    state: EmailVerificationUiState,
    onAction: (EmailVerificationAction) -> Unit,
) {
    AuthFormLayout(
        // No back arrow: the account already exists, so "back" is the explicit sign-out below.
        topBar = { AuthTopBar(onBackClick = {}, showBackButton = false) },
        footer = {
            if (state.isStillUnverified) {
                Text(
                    text = stringResource(Res.string.email_verification_still_unverified),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }
            state.error?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
                Spacer(modifier = Modifier.height(Spacing.md))
            }
            PrimaryButton(
                text = stringResource(Res.string.email_verification_check_button),
                onClick = { onAction(EmailVerificationAction.OnCheckVerification) },
                isLoading = state.isChecking
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            SecondaryButton(
                text = if (state.resendCountdown > 0) {
                    stringResource(Res.string.email_verification_resend_countdown, state.resendCountdown)
                } else {
                    stringResource(Res.string.email_verification_resend)
                },
                onClick = { onAction(EmailVerificationAction.OnResendEmail) },
                enabled = state.resendCountdown == 0 && !state.isLoading
            )
            TextButton(
                onClick = { onAction(EmailVerificationAction.OnUseAnotherEmail) },
                enabled = !state.isLoading,
            ) {
                Text(
                    text = stringResource(Res.string.email_verification_wrong_email),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        },
    ) {
        AuthHeader(
            title = stringResource(Res.string.email_verification_title),
            subtitle = stringResource(Res.string.email_verification_subtitle, state.obfuscatedEmail),
            icon = vectorResource(Res.drawable.mail_24px),
            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        VerificationSteps(
            steps = listOf(
                stringResource(Res.string.email_verification_step_1),
                stringResource(Res.string.email_verification_step_2),
                stringResource(Res.string.email_verification_step_3),
            )
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        AuthNote(text = stringResource(Res.string.auth_spam_hint))
    }
}

/** A short numbered checklist; the numbers are decorative, the order is in the text. */
@Composable
private fun VerificationSteps(steps: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        steps.forEachIndexed { index, step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Text(text = step, style = MaterialTheme.typography.bodyLarge)
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
        )
    }
}
