package com.juanpablo0612.carpool.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.presentation.auth.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.profile_delete_account_confirm_button
import enrutadoseia.composeapp.generated.resources.profile_delete_account_confirm_desc
import enrutadoseia.composeapp.generated.resources.profile_delete_account_confirm_title
import enrutadoseia.composeapp.generated.resources.profile_delete_account_name_hint
import enrutadoseia.composeapp.generated.resources.profile_delete_account_name_mismatch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeleteAccountDialog(
    nameInput: String,
    expectedName: String,
    isLoading: Boolean,
    error: AuthError?,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.profile_delete_account_confirm_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(Res.string.profile_delete_account_confirm_desc))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = onNameChange,
                    placeholder = { Text(stringResource(Res.string.profile_delete_account_name_hint)) },
                    isError = nameInput.isNotEmpty() && nameInput != expectedName,
                    supportingText = if (nameInput.isNotEmpty() && nameInput != expectedName) {
                        { Text(stringResource(Res.string.profile_delete_account_name_mismatch)) }
                    } else null,
                    singleLine = true
                )
                if (error != null) {
                    ErrorMessage(message = stringResource(error.asStringResource()))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = nameInput == expectedName && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                else Text(stringResource(Res.string.profile_delete_account_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        }
    )
}
