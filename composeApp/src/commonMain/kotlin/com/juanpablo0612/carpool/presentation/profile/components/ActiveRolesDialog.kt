package com.juanpablo0612.carpool.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.active_roles_close
import enrutadoseia.composeapp.generated.resources.active_roles_driver
import enrutadoseia.composeapp.generated.resources.active_roles_min_one
import enrutadoseia.composeapp.generated.resources.active_roles_passenger
import enrutadoseia.composeapp.generated.resources.active_roles_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ActiveRolesDialog(
    user: User?,
    blocked: Boolean,
    onToggleRole: (UserRole, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.active_roles_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (blocked) {
                    Text(
                        text = stringResource(Res.string.active_roles_min_one),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.active_roles_driver))
                    Switch(
                        checked = user?.isDriver == true,
                        onCheckedChange = { onToggleRole(UserRole.Driver, it) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.active_roles_passenger))
                    Switch(
                        checked = user?.isPassenger == true,
                        onCheckedChange = { onToggleRole(UserRole.Passenger, it) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.active_roles_close))
            }
        }
    )
}
