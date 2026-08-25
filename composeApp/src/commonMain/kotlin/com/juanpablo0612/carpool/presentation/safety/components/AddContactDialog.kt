package com.juanpablo0612.carpool.presentation.safety.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.safety.SafetyContactFieldError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.safety_add_contact_title
import enrutadoseia.composeapp.generated.resources.safety_contact_name_label
import enrutadoseia.composeapp.generated.resources.safety_contact_name_placeholder
import enrutadoseia.composeapp.generated.resources.safety_contact_phone_label
import enrutadoseia.composeapp.generated.resources.safety_contact_phone_placeholder
import enrutadoseia.composeapp.generated.resources.safety_save_contact
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddContactDialog(
    name: String,
    phone: String,
    nameError: SafetyContactFieldError?,
    phoneError: SafetyContactFieldError?,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.safety_add_contact_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(Res.string.safety_contact_name_label)) },
                    placeholder = { Text(stringResource(Res.string.safety_contact_name_placeholder)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(stringResource(it.asStringResource())) } },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text(stringResource(Res.string.safety_contact_phone_label)) },
                    placeholder = { Text(stringResource(Res.string.safety_contact_phone_placeholder)) },
                    isError = phoneError != null,
                    supportingText = phoneError?.let { { Text(stringResource(it.asStringResource())) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !isSaving) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                else Text(stringResource(Res.string.safety_save_contact))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel_button)) }
        }
    )
}
