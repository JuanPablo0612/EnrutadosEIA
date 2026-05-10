package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolTopBar(
    title: String,
    user: User,
    isDualRole: Boolean,
    currentRoleLabel: String = "",
    onAvatarClick: () -> Unit,
    onRoleToggle: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user.name ?: user.email).first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        actions = {
            if (isDualRole && onRoleToggle != null) {
                FilterChip(
                    selected = false,
                    onClick = onRoleToggle,
                    label = { Text(currentRoleLabel) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            actions()
        }
    )
}
