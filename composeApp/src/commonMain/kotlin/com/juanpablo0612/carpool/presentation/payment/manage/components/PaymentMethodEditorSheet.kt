package com.juanpablo0612.carpool.presentation.payment.manage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidator
import com.juanpablo0612.carpool.presentation.payment.asStringResource
import com.juanpablo0612.carpool.presentation.payment.components.QrImage
import com.juanpablo0612.carpool.presentation.payment.description
import com.juanpablo0612.carpool.presentation.payment.label
import com.juanpablo0612.carpool.presentation.payment.manage.PaymentMethodEditor
import com.juanpablo0612.carpool.presentation.payment.manage.PaymentMethodsAction
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.payment_bre_b_key_label
import enrutadoseia.composeapp.generated.resources.payment_bre_b_key_placeholder
import enrutadoseia.composeapp.generated.resources.payment_qr_change_image
import enrutadoseia.composeapp.generated.resources.payment_qr_image_cd
import enrutadoseia.composeapp.generated.resources.payment_save
import org.jetbrains.compose.resources.stringResource

/**
 * The form for the method being added. Each [PaymentMethodEditor] kind brings its own fields;
 * the title, save button and error slot are shared.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PaymentMethodEditorSheet(
    editor: PaymentMethodEditor,
    isSaving: Boolean,
    errorMessage: String?,
    onAction: (PaymentMethodsAction) -> Unit,
    onPickQrImage: () -> Unit,
) {
    val type = when (editor) {
        is PaymentMethodEditor.BreBKey -> PaymentMethodType.BreBKey
        is PaymentMethodEditor.QrCode -> PaymentMethodType.QrCode
    }
    val save = { onAction(PaymentMethodsAction.OnSave) }
    ModalBottomSheet(
        onDismissRequest = { onAction(PaymentMethodsAction.OnDismissEditor) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(type.label),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(type.description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            val fieldError = editor.fieldError?.let { stringResource(it.asStringResource()) }
            when (editor) {
                is PaymentMethodEditor.BreBKey -> CarpoolTextField(
                    value = editor.key,
                    onValueChange = { onAction(PaymentMethodsAction.OnBreBKeyChange(it.take(PaymentMethodValidator.BRE_B_KEY_MAX_LENGTH + 1))) },
                    label = stringResource(Res.string.payment_bre_b_key_label),
                    placeholder = stringResource(Res.string.payment_bre_b_key_placeholder),
                    errorMessage = fieldError,
                    enabled = !isSaving,
                    // Keys are identifiers: no autocorrect or capitals, and Done saves.
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { save() }),
                )
                is PaymentMethodEditor.QrCode -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    editor.imageFile?.let {
                        QrImage(model = it, contentDescription = stringResource(Res.string.payment_qr_image_cd))
                    }
                    SecondaryButton(
                        text = stringResource(Res.string.payment_qr_change_image),
                        onClick = onPickQrImage,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    fieldError?.let { ErrorMessage(message = it, modifier = Modifier.fillMaxWidth()) }
                }
            }

            errorMessage?.let { ErrorMessage(message = it, modifier = Modifier.fillMaxWidth()) }

            PrimaryButton(
                text = stringResource(Res.string.payment_save),
                onClick = save,
                isLoading = isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
