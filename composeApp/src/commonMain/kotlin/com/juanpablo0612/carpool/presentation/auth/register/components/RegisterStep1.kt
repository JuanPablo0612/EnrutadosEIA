package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.EmailTextField
import com.juanpablo0612.carpool.presentation.ui.components.PasswordTextField
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_password_label
import enrutadoseia.composeapp.generated.resources.email_label
import enrutadoseia.composeapp.generated.resources.email_placeholder
import enrutadoseia.composeapp.generated.resources.full_name_label
import enrutadoseia.composeapp.generated.resources.full_name_placeholder
import enrutadoseia.composeapp.generated.resources.password_label
import enrutadoseia.composeapp.generated.resources.password_placeholder
import enrutadoseia.composeapp.generated.resources.register_continue_button
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RegisterStep1(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        NameTextField(
            value = state.fullName,
            onValueChange = { onAction(RegisterAction.OnFullNameChanged(it)) },
            label = stringResource(Res.string.full_name_label),
            placeholder = stringResource(Res.string.full_name_placeholder),
            errorMessage = state.fullNameError?.asStringResource()?.let { stringResource(it) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        EmailTextField(
            value = state.email,
            onValueChange = { onAction(RegisterAction.OnEmailChanged(it)) },
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.email_placeholder),
            errorMessage = state.emailError?.asStringResource()?.let { stringResource(it) },
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

        if (state.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            PasswordStrengthIndicator(password = state.password)
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

        Spacer(modifier = Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.register_continue_button),
            onClick = { onAction(RegisterAction.OnNextStep) }
        )
    }
}
