package com.juanpablo0612.carpool.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun ProfileListItem(title: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = icon,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
