package com.juanpablo0612.carpool.presentation.safety.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_remove_contact
import enrutadoseia.composeapp.generated.resources.delete_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun ContactItem(contact: EmergencyContact, onRemove: () -> Unit) {
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
