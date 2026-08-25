package com.juanpablo0612.carpool.presentation.auth.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.register.components.RegisterStep1
import com.juanpablo0612.carpool.presentation.auth.register.components.RegisterStep2
import com.juanpablo0612.carpool.presentation.auth.register.components.RegisterStep3
import com.juanpablo0612.carpool.presentation.auth.register.components.StepIndicator
import com.juanpablo0612.carpool.presentation.ui.components.*
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (com.juanpablo0612.carpool.domain.auth.model.User) -> Unit,
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
    val stepTitle = when (state.currentStep) {
        1 -> stringResource(Res.string.register_step_1_title)
        2 -> stringResource(Res.string.register_step_2_title)
        else -> stringResource(Res.string.register_step_3_title)
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.register_title),
                onBackClick = {
                    if (state.currentStep > 1) onAction(RegisterAction.OnPreviousStep)
                    else onBackClick()
                }
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

            CompactAuthHeader(screenTitle = stepTitle)

            Spacer(modifier = Modifier.height(Spacing.md))

            StepIndicator(
                current = state.currentStep,
                total = 3,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = stringResource(Res.string.register_step_indicator, state.currentStep, 3),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = Spacing.xs)
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            AnimatedContent(targetState = state.currentStep) { step ->
                when (step) {
                    1 -> RegisterStep1(state = state, onAction = onAction)
                    2 -> RegisterStep2(state = state, onAction = onAction)
                    else -> RegisterStep3(
                        state = state,
                        onAction = onAction,
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Preview
@Composable
private fun RegisterStep1Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(currentStep = 1),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun RegisterStep3Preview() {
    CarpoolTheme {
        RegisterContent(
            state = RegisterUiState(currentStep = 3),
            onAction = {},
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}
