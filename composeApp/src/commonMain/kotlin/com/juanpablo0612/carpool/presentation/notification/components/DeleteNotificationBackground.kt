package com.juanpablo0612.carpool.presentation.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_delete_notification
import enrutadoseia.composeapp.generated.resources.delete_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun DeleteNotificationBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // 2dp: matches NotificationItem's own fine visual gap, below the 4dp scale step.
            .padding(horizontal = Spacing.screenHorizontal, vertical = 2.dp)
            .clip(CardDefaults.shape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 20.dp), // aligns the icon within the revealed background, not on the spacing scale
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.delete_24px),
            contentDescription = stringResource(Res.string.cd_delete_notification),
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
