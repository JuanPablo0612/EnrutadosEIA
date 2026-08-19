package com.juanpablo0612.carpool.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.profile_edit_button
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UserHeader(user: User?, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(
            name = user?.name ?: user?.email ?: "?",
            photoUrl = user?.photoUrl,
            size = 96.dp,
        )
        Text(
            text = user?.name ?: user?.email ?: "",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        if (user?.name != null) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(onClick = onEditClick) {
            Text(stringResource(Res.string.profile_edit_button))
        }
    }
}
