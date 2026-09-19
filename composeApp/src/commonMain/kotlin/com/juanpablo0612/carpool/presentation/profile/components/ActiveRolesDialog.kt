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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.active_roles_close
import enrutadoseia.composeapp.generated.resources.active_roles_disable_confirm_body
import enrutadoseia.composeapp.generated.resources.active_roles_disable_confirm_button
import enrutadoseia.composeapp.generated.resources.active_roles_disable_confirm_title
import enrutadoseia.composeapp.generated.resources.active_roles_driver
import enrutadoseia.composeapp.generated.resources.active_roles_min_one
import enrutadoseia.composeapp.generated.resources.active_roles_passenger
import enrutadoseia.composeapp.generated.resources.active_roles_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ActiveRolesDialog(
    user: User?,
    activeRole: UserRole?,
    blocked: Boolean,
    onToggleRole: (UserRole, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    // Disabling the role you're currently active in switches you into the other role right
    // away (see ProfileViewModel.handleRoleToggle) — confirm first instead of applying silently,
    // consistent with every other consequential action in the app going through ConfirmDialog.
    var pendingDisableRole by remember { mutableStateOf<UserRole?>(null) }

    fun requestToggle(role: UserRole, enabled: Boolean) {
        if (!enabled && role == activeRole) {
            pendingDisableRole = role
        } else {
            onToggleRole(role, enabled)
        }
    }

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
                        onCheckedChange = { requestToggle(UserRole.Driver, it) }
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
                        onCheckedChange = { requestToggle(UserRole.Passenger, it) }
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

    pendingDisableRole?.let { role ->
        ConfirmDialog(
            title = stringResource(Res.string.active_roles_disable_confirm_title),
            description = stringResource(Res.string.active_roles_disable_confirm_body),
            confirmText = stringResource(Res.string.active_roles_disable_confirm_button),
            onConfirm = {
                onToggleRole(role, false)
                pendingDisableRole = null
            },
            onDismiss = { pendingDisableRole = null },
            isDestructive = true
        )
    }
}
