package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.ui.theme.Alpha
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.photo_camera_24px
import enrutadoseia.composeapp.generated.resources.vehicle_change_photo
import enrutadoseia.composeapp.generated.resources.vehicle_photo_hint
import enrutadoseia.composeapp.generated.resources.vehicle_photo_section
import enrutadoseia.composeapp.generated.resources.vehicle_photo_tap_to_add
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

// 1. Photo
@Composable
internal fun VehiclePhotoSection(
    photoFile: PlatformFile?,
    existingPhotoUrl: String?,
    onShowPhotoSheet: () -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_photo_section),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // component-intrinsic: fixed photo-preview height, not a spacing value
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable { onShowPhotoSheet() },
        contentAlignment = Alignment.Center
    ) {
        val hasPhoto = photoFile != null || existingPhotoUrl != null
        if (hasPhoto) {
            AsyncImage(
                model = photoFile ?: existingPhotoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = Alpha.SCRIM))
                    .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.vehicle_change_photo),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(Spacing.lg)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.photo_camera_24px),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp), // component-intrinsic icon size
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = stringResource(Res.string.vehicle_photo_tap_to_add),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = stringResource(Res.string.vehicle_photo_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Spacer(Modifier.height(Spacing.xl))
}
