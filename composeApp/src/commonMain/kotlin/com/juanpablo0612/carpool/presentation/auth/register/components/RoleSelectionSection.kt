package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.driver_option
import enrutadoseia.composeapp.generated.resources.passenger_option
import enrutadoseia.composeapp.generated.resources.user_type_prompt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RoleSelectionSection(
    isPassenger: Boolean,
    isDriver: Boolean,
    onPassengerToggle: (Boolean) -> Unit,
    onDriverToggle: (Boolean) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.user_type_prompt),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = Spacing.md)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            RoleSelectionCard(
                title = stringResource(Res.string.passenger_option),
                isSelected = isPassenger,
                onClick = { onPassengerToggle(!isPassenger) },
                modifier = Modifier.weight(1f)
            )
            RoleSelectionCard(
                title = stringResource(Res.string.driver_option),
                isSelected = isDriver,
                onClick = { onDriverToggle(!isDriver) },
                modifier = Modifier.weight(1f)
            )
        }
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            ErrorMessage(message = errorMessage)
        }
    }
}
