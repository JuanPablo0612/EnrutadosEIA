package com.juanpablo0612.carpool.presentation.safety

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.call_24px
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.cd_remove_contact
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.safety_add_contact
import enrutadoseia.composeapp.generated.resources.safety_add_contact_title
import enrutadoseia.composeapp.generated.resources.safety_auto_share
import enrutadoseia.composeapp.generated.resources.safety_auto_share_desc
import enrutadoseia.composeapp.generated.resources.safety_contact_name_label
import enrutadoseia.composeapp.generated.resources.safety_contact_name_placeholder
import enrutadoseia.composeapp.generated.resources.safety_contact_phone_label
import enrutadoseia.composeapp.generated.resources.safety_contact_phone_placeholder
import enrutadoseia.composeapp.generated.resources.safety_contacts_section
import enrutadoseia.composeapp.generated.resources.safety_description
import enrutadoseia.composeapp.generated.resources.safety_max_contacts_reached
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_body
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_confirm
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_title
import enrutadoseia.composeapp.generated.resources.safety_save_contact
import enrutadoseia.composeapp.generated.resources.safety_settings_section
import enrutadoseia.composeapp.generated.resources.safety_title
import enrutadoseia.composeapp.generated.resources.safety_vibrate_sos
import enrutadoseia.composeapp.generated.resources.safety_vibrate_sos_desc
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SafetyScreen(
    viewModel: SafetyViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SafetyEvent.NavigateBack -> onBackClick()
        }
    }

    SafetyContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyContent(
    state: SafetyUiState,
    onAction: (SafetyAction) -> Unit
) {
    state.pendingRemoveContactId?.let { contactId ->
        ConfirmDialog(
            title = stringResource(Res.string.safety_remove_contact_title),
            description = stringResource(Res.string.safety_remove_contact_body),
            confirmText = stringResource(Res.string.safety_remove_contact_confirm),
            onConfirm = { onAction(SafetyAction.OnConfirmRemoveContact(contactId)) },
            onDismiss = { onAction(SafetyAction.OnDismissRemoveContact) },
            isDestructive = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.safety_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(SafetyAction.OnBackClick) }) {
                        Icon(vectorResource(Res.drawable.arrow_back_24px), null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(Res.string.safety_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                SectionHeader(stringResource(Res.string.safety_contacts_section))

                if (state.contacts.isEmpty()) {
                    EmptyState(
                        icon = vectorResource(Res.drawable.call_24px),
                        title = stringResource(Res.string.safety_add_contact_title),
                        description = stringResource(Res.string.safety_description),
                        primaryAction = ActionButton(
                            label = stringResource(Res.string.safety_add_contact),
                            onClick = { onAction(SafetyAction.OnAddContactClick) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                } else {
                    state.contacts.forEach { contact ->
                        ContactItem(
                            contact = contact,
                            onRemove = { onAction(SafetyAction.OnRemoveContact(contact.id)) }
                        )
                    }

                    if (state.canAddContact) {
                        TextButton(
                            onClick = { onAction(SafetyAction.OnAddContactClick) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(stringResource(Res.string.safety_add_contact))
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.safety_max_contacts_reached),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                SectionHeader(stringResource(Res.string.safety_settings_section))

                ListItem(
                    headlineContent = { Text(stringResource(Res.string.safety_auto_share)) },
                    supportingContent = { Text(stringResource(Res.string.safety_auto_share_desc)) },
                    trailingContent = {
                        Switch(
                            checked = state.settings.autoShareTrip,
                            onCheckedChange = { onAction(SafetyAction.OnToggleAutoShare(it)) }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(Res.string.safety_vibrate_sos)) },
                    supportingContent = { Text(stringResource(Res.string.safety_vibrate_sos_desc)) },
                    trailingContent = {
                        Switch(
                            checked = state.settings.vibrateSos,
                            onCheckedChange = { onAction(SafetyAction.OnToggleVibrateSos(it)) }
                        )
                    }
                )
            }

            if (state.showAddDialog) {
                AddContactDialog(
                    name = state.newContactName,
                    phone = state.newContactPhone,
                    nameError = state.newContactNameError,
                    phoneError = state.newContactPhoneError,
                    isSaving = state.isSaving,
                    onNameChange = { onAction(SafetyAction.OnContactNameChange(it)) },
                    onPhoneChange = { onAction(SafetyAction.OnContactPhoneChange(it)) },
                    onSave = { onAction(SafetyAction.OnSaveContact) },
                    onDismiss = { onAction(SafetyAction.OnDismissAddDialog) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ContactItem(contact: EmergencyContact, onRemove: () -> Unit) {
    ListItem(
        headlineContent = { Text(contact.name, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(contact.phone) },
        trailingContent = {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = vectorResource(Res.drawable.delete_24px),
                    contentDescription = stringResource(Res.string.cd_remove_contact),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
private fun AddContactDialog(
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
