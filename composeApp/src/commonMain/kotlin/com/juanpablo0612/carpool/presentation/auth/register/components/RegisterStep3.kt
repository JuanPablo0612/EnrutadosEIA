package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import com.juanpablo0612.carpool.presentation.auth.register.RegisterAction
import com.juanpablo0612.carpool.presentation.auth.register.RegisterUiState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.LinkText
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.already_have_account_question
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.create_account_button
import enrutadoseia.composeapp.generated.resources.error_terms_not_accepted
import enrutadoseia.composeapp.generated.resources.login_link
import enrutadoseia.composeapp.generated.resources.register_terms_checkbox
import enrutadoseia.composeapp.generated.resources.terms_and_privacy
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun RegisterStep3(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        RoleSelectionSection(
            isPassenger = state.isPassenger,
            isDriver = state.isDriver,
            onPassengerToggle = { onAction(RegisterAction.OnPassengerChanged(it)) },
            onDriverToggle = { onAction(RegisterAction.OnDriverChanged(it)) },
            errorMessage = state.roleError?.asStringResource()?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.hasAcceptedTerms,
                onCheckedChange = { onAction(RegisterAction.OnTermsChanged(it)) }
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = stringResource(Res.string.register_terms_checkbox),
                style = MaterialTheme.typography.bodySmall,
                color = if (state.termsError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }

        if (state.termsError) {
            Text(
                text = stringResource(Res.string.error_terms_not_accepted),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.lg)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(Spacing.lg))
            ErrorMessage(message = stringResource(it.asStringResource()))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.create_account_button),
            onClick = { onAction(RegisterAction.OnRegisterClicked) },
            isLoading = state.isLoading,
            trailingIcon = vectorResource(Res.drawable.arrow_forward_24px)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.already_have_account_question),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            LinkText(
                text = stringResource(Res.string.login_link),
                onClick = onNavigateToLogin
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = stringResource(Res.string.terms_and_privacy),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
    }
}
