package com.juanpablo0612.carpool.presentation.auth.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.components.RegisterStep1
import com.juanpablo0612.carpool.presentation.auth.register.components.RegisterStep2
import com.juanpablo0612.carpool.presentation.ui.components.AuthFormLayout
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.already_have_account_question
import enrutadoseia.composeapp.generated.resources.create_account_button
import enrutadoseia.composeapp.generated.resources.login_link
import enrutadoseia.composeapp.generated.resources.register_continue_button
import enrutadoseia.composeapp.generated.resources.register_step_indicator
import org.jetbrains.compose.resources.stringResource

private const val REGISTER_STEP_COUNT = 2

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (User) -> Unit,
    onNavigateToEmailVerification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateAfterAuth -> onRegisterSuccess(event.user)
            AuthEvent.NavigateToEmailVerification -> onNavigateToEmailVerification()
        }
    }

    RegisterContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToLogin = onNavigateToLogin,
        onBackClick = onBackClick
    )
}

@Composable
fun RegisterContent(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val isFirstStep = state.currentStep == 1

    AuthFormLayout(
        topBar = {
            AuthTopBar(
                onBackClick = {
                    if (isFirstStep) onBackClick() else onAction(RegisterAction.OnPreviousStep)
                },
            ) {
                RegisterProgress(current = state.currentStep, total = REGISTER_STEP_COUNT)
            }
        },
        footer = {
            state.error?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
                Spacer(modifier = Modifier.height(Spacing.md))
            }
            if (isFirstStep) {
                PrimaryButton(
                    text = stringResource(Res.string.register_continue_button),
                    onClick = { onAction(RegisterAction.OnNextStep) }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.already_have_account_question),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(text = stringResource(Res.string.login_link), style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                PrimaryButton(
                    text = stringResource(Res.string.create_account_button),
                    onClick = { onAction(RegisterAction.OnRegisterClicked) },
                    isLoading = state.isLoading
                )
            }
        },
    ) {
        AnimatedContent(targetState = state.currentStep, label = "registerStep") { step ->
            when (step) {
                1 -> RegisterStep1(state = state, onAction = onAction)
                else -> RegisterStep2(state = state, onAction = onAction)
            }
        }
    }
}

/** A determinate bar plus "1 de 2", sitting beside the back button. */
@Composable
private fun RowScope.RegisterProgress(current: Int, total: Int) {
    LinearProgressIndicator(
        progress = { current.toFloat() / total },
        modifier = Modifier
            .weight(1f)
            // The bar's own thickness, not a spacing step.
            .height(6.dp)
            .clip(MaterialTheme.shapes.extraSmall),
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        drawStopIndicator = {},
        gapSize = 0.dp,
    )
    Text(
        text = stringResource(Res.string.register_step_indicator, current, total),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = Spacing.md),
    )
}

@Preview
@Composable
private fun RegisterStep1Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(fullName = "Juan Pérez", email = "juan.perez"),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun RegisterStep2Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(currentStep = 2, phone = "+573001234567", hasAcceptedTerms = true),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}
