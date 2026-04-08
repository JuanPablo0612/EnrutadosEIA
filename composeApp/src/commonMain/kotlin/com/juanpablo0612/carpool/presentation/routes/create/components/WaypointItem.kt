package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import org.jetbrains.compose.resources.vectorResource

@Composable
fun WaypointItem(
    place: Place,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.location_on_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        
        if (!isReadOnly) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = vectorResource(Res.drawable.delete_24px),
                    contentDescription = "Remove waypoint",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
