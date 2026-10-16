package com.juanpablo0612.carpool.presentation.payment.pay.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.presentation.payment.components.QrImage
import com.juanpablo0612.carpool.presentation.payment.icon
import com.juanpablo0612.carpool.presentation.payment.label
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.check_24px
import enrutadoseia.composeapp.generated.resources.content_copy_24px
import enrutadoseia.composeapp.generated.resources.download_24px
import enrutadoseia.composeapp.generated.resources.payment_bre_b_copied
import enrutadoseia.composeapp.generated.resources.payment_bre_b_copy
import enrutadoseia.composeapp.generated.resources.payment_bre_b_hint
import enrutadoseia.composeapp.generated.resources.payment_qr_hint
import enrutadoseia.composeapp.generated.resources.payment_qr_image_cd
import enrutadoseia.composeapp.generated.resources.payment_qr_save
import enrutadoseia.composeapp.generated.resources.payment_qr_saved
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
private fun MethodHeader(method: PaymentMethod, hint: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Icon(vectorResource(method.type.icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = stringResource(method.type.label), style = MaterialTheme.typography.titleMedium)
    }
    Text(text = hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** A Bre-B key, selectable and copyable in one tap, ready to paste into the bank app. */
@Composable
internal fun BreBKeyPayCard(
    method: PaymentMethod.BreBKey,
    isCopied: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            MethodHeader(method, stringResource(Res.string.payment_bre_b_hint))
            SelectionContainer {
                Text(
                    text = method.key,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            SecondaryButton(
                text = stringResource(if (isCopied) Res.string.payment_bre_b_copied else Res.string.payment_bre_b_copy),
                onClick = onCopy,
                leadingIcon = vectorResource(if (isCopied) Res.drawable.check_24px else Res.drawable.content_copy_24px),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The driver's QR, large enough to scan from another phone, with a way to keep it for the bank app. */
@Composable
internal fun QrCodePayCard(
    method: PaymentMethod.QrCode,
    isSaving: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                MethodHeader(method, stringResource(Res.string.payment_qr_hint))
            }
            QrImage(model = method.imageUrl, contentDescription = stringResource(Res.string.payment_qr_image_cd))
            PrimaryButton(
                text = stringResource(Res.string.payment_qr_save),
                onClick = onSave,
                isLoading = isSaving,
                leadingIcon = vectorResource(Res.drawable.download_24px),
                modifier = Modifier.fillMaxWidth(),
            )
            if (isSaved) {
                SuccessMessage(message = stringResource(Res.string.payment_qr_saved), modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
