package com.juanpablo0612.carpool.presentation.payment.manage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.presentation.payment.components.QrImage
import com.juanpablo0612.carpool.presentation.payment.icon
import com.juanpablo0612.carpool.presentation.payment.label
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.payment_delete_cd
import enrutadoseia.composeapp.generated.resources.payment_qr_image_cd
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** One of the driver's own methods, as passengers will see it, with a way to remove it. */
@Composable
internal fun PaymentMethodCard(
    method: PaymentMethod,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(method.type.label)
    CarpoolListCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Icon(
                    imageVector = vectorResource(method.type.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.delete_24px),
                        contentDescription = stringResource(Res.string.payment_delete_cd, label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            when (method) {
                is PaymentMethod.BreBKey -> Text(
                    text = method.key,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                is PaymentMethod.QrCode -> QrImage(
                    model = method.imageUrl,
                    contentDescription = stringResource(Res.string.payment_qr_image_cd),
                    maxWidth = 200.dp,
                )
            }
        }
    }
}
