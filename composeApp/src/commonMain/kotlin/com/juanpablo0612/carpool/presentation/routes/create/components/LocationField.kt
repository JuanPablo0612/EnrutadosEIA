package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.select_location_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationField(
    label: String,
    place: Place?,
    isLocked: Boolean,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(enabled = !isLocked, onClick = onClick),
        colors = if (isLocked) CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                 else CardDefaults.outlinedCardColors()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                place?.name ?: stringResource(Res.string.select_location_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = if (place == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
