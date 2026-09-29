package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.EiaEmailTextField
import com.juanpablo0612.carpool.presentation.ui.components.PasswordTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_password_label
import enrutadoseia.composeapp.generated.resources.email_label
import enrutadoseia.composeapp.generated.resources.email_placeholder
import enrutadoseia.composeapp.generated.resources.full_name_label
import enrutadoseia.composeapp.generated.resources.full_name_placeholder
import enrutadoseia.composeapp.generated.resources.password_label
import enrutadoseia.composeapp.generated.resources.password_placeholder
import enrutadoseia.composeapp.generated.resources.register_email_hint
import enrutadoseia.composeapp.generated.resources.register_step_1_subtitle
import enrutadoseia.composeapp.generated.resources.register_step_1_title
import org.jetbrains.compose.resources.stringResource

/** The account step: name, institutional email and password. */
@Composable
internal fun RegisterStep1(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AuthHeader(
            title = stringResource(Res.string.register_step_1_title),
            subtitle = stringResource(Res.string.register_step_1_subtitle),
            icon = null,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        NameTextField(
            value = state.fullName,
            onValueChange = { onAction(RegisterAction.OnFullNameChanged(it)) },
            label = stringResource(Res.string.full_name_label),
            placeholder = stringResource(Res.string.full_name_placeholder),
            errorMessage = state.fullNameError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        EiaEmailTextField(
            value = state.email,
            onValueChange = { onAction(RegisterAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
            supportingText = { Text(stringResource(Res.string.register_email_hint)) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        PasswordTextField(
            value = state.password,
            onValueChange = { onAction(RegisterAction.OnPasswordChanged(it)) },
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder),
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { onAction(RegisterAction.OnTogglePasswordVisibility) },
            errorMessage = state.passwordError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        state.passwordStrength?.let { strength ->
            Spacer(modifier = Modifier.height(Spacing.sm))
            PasswordStrengthIndicator(strength = strength)
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        PasswordTextField(
            value = state.confirmPassword,
            onValueChange = { onAction(RegisterAction.OnConfirmPasswordChanged(it)) },
            label = stringResource(Res.string.confirm_password_label),
            placeholder = stringResource(Res.string.password_placeholder),
            isPasswordVisible = state.isConfirmPasswordVisible,
            onTogglePasswordVisibility = { onAction(RegisterAction.OnToggleConfirmPasswordVisibility) },
            errorMessage = state.confirmPasswordError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onAction(RegisterAction.OnNextStep) })
        )
    }
}
