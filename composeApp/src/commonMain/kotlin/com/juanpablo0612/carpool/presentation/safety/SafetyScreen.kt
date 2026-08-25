package com.juanpablo0612.carpool.presentation.safety

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.safety.components.AddContactDialog
import com.juanpablo0612.carpool.presentation.safety.components.ContactItem
import com.juanpablo0612.carpool.presentation.safety.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.call_24px
import enrutadoseia.composeapp.generated.resources.safety_add_contact
import enrutadoseia.composeapp.generated.resources.safety_add_contact_title
import enrutadoseia.composeapp.generated.resources.safety_auto_share
import enrutadoseia.composeapp.generated.resources.safety_auto_share_desc
import enrutadoseia.composeapp.generated.resources.safety_contacts_section
import enrutadoseia.composeapp.generated.resources.safety_description
import enrutadoseia.composeapp.generated.resources.safety_max_contacts_reached
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_body
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_confirm
import enrutadoseia.composeapp.generated.resources.safety_remove_contact_title
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
            CarpoolBackTopBar(
                title = stringResource(Res.string.safety_title),
                onBack = { onAction(SafetyAction.OnBackClick) },
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
                    .imePadding()
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
