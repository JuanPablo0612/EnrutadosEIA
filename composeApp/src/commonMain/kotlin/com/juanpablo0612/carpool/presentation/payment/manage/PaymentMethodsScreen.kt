package com.juanpablo0612.carpool.presentation.payment.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.presentation.payment.manage.components.PaymentMethodCard
import com.juanpablo0612.carpool.presentation.payment.manage.components.PaymentMethodEditorSheet
import com.juanpablo0612.carpool.presentation.payment.manage.components.PaymentTypePickerSheet
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.error_payment_load_failed
import enrutadoseia.composeapp.generated.resources.payment_add_method
import enrutadoseia.composeapp.generated.resources.payment_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.payment_delete_confirm_description
import enrutadoseia.composeapp.generated.resources.payment_delete_confirm_title
import enrutadoseia.composeapp.generated.resources.payment_methods_empty_description
import enrutadoseia.composeapp.generated.resources.payment_methods_empty_title
import enrutadoseia.composeapp.generated.resources.payment_methods_subtitle
import enrutadoseia.composeapp.generated.resources.payment_methods_title
import enrutadoseia.composeapp.generated.resources.payments_24px
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PaymentMethodsScreen(
    viewModel: PaymentMethodsViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PaymentMethodsEvent.NavigateBack -> onBackClick()
        }
    }

    PaymentMethodsContent(state = state, onAction = viewModel::onAction)
}

@Composable
fun PaymentMethodsContent(
    state: PaymentMethodsUiState,
    onAction: (PaymentMethodsAction) -> Unit,
) {
    // QR codes are screenshots from the bank app, so the gallery is the only source offered.
    val qrPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        onAction(PaymentMethodsAction.OnQrImagePicked(file))
    }

    if (state.showTypePicker) {
        PaymentTypePickerSheet(
            onTypeSelected = { type ->
                onAction(PaymentMethodsAction.OnTypeSelected(type))
                if (type == PaymentMethodType.QrCode) qrPicker.launch()
            },
            onDismiss = { onAction(PaymentMethodsAction.OnDismissTypePicker) },
        )
    }

    state.editor?.let { editor ->
        // A QR editor opens behind the image picker and only shows once an image is chosen.
        val waitingForImage = editor is PaymentMethodEditor.QrCode && editor.imageFile == null
        if (!waitingForImage) {
            PaymentMethodEditorSheet(
                editor = editor,
                isSaving = state.isSaving,
                errorMessage = state.error?.let { stringResource(it.asStringResource()) },
                onAction = onAction,
                onPickQrImage = { qrPicker.launch() },
            )
        }
    }

    if (state.methodToDelete != null) {
        ConfirmDialog(
            title = stringResource(Res.string.payment_delete_confirm_title),
            description = stringResource(Res.string.payment_delete_confirm_description),
            confirmText = stringResource(Res.string.payment_delete_confirm_button),
            onConfirm = { onAction(PaymentMethodsAction.OnConfirmDelete) },
            onDismiss = { onAction(PaymentMethodsAction.OnDismissDelete) },
            isDestructive = true,
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.payment_methods_title),
                subtitle = stringResource(Res.string.payment_methods_subtitle),
                onBack = { onAction(PaymentMethodsAction.OnBackClick) },
            )
        },
        floatingActionButton = {
            if (!state.isLoading && !state.loadFailed && state.methods.isNotEmpty() && state.canAddMore) {
                ExtendedFloatingActionButton(
                    onClick = { onAction(PaymentMethodsAction.OnAddClick) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(vectorResource(Res.drawable.add_24px), contentDescription = null) },
                    text = { Text(stringResource(Res.string.payment_add_method)) },
                )
            }
        },
    ) { padding ->
        val modifier = Modifier.fillMaxSize().padding(padding)
        when {
            state.isLoading -> ListSkeleton(modifier = modifier)
            state.loadFailed -> ErrorState(
                description = stringResource(Res.string.error_payment_load_failed),
                onRetry = { onAction(PaymentMethodsAction.OnRetry) },
                modifier = modifier,
            )
            state.methods.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.payments_24px),
                title = stringResource(Res.string.payment_methods_empty_title),
                description = stringResource(Res.string.payment_methods_empty_description),
                primaryAction = ActionButton(stringResource(Res.string.payment_add_method)) {
                    onAction(PaymentMethodsAction.OnAddClick)
                },
                modifier = modifier,
            )
            else -> CenteredContent(ContentWidth.list, modifier = modifier, gutter = 0.dp) { margin ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Bottom room so the FAB never covers the last card's delete button.
                    contentPadding = PaddingValues(
                        start = Spacing.lg,
                        end = Spacing.lg,
                        top = Spacing.lg,
                        bottom = 96.dp,
                    ).plusHorizontal(margin),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    // The editor sheet shows save errors itself; the list shows the rest.
                    val listError = state.error.takeIf { state.editor == null }
                    if (listError != null) {
                        item(key = "error") {
                            ErrorMessage(
                                message = stringResource(listError.asStringResource()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAction(PaymentMethodsAction.OnDismissError) },
                            )
                        }
                    }
                    items(state.methods, key = { it.id }) { method ->
                        PaymentMethodCard(
                            method = method,
                            onDelete = { onAction(PaymentMethodsAction.OnDeleteRequest(method)) },
                        )
                    }
                }
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun PaymentMethodsContentPreview() {
    CarpoolTheme {
        PaymentMethodsContent(
            state = PaymentMethodsUiState(
                isLoading = false,
                methods = listOf(
                    PaymentMethod.BreBKey(id = "1", key = "@carolina.r"),
                    PaymentMethod.QrCode(id = "2", imageUrl = ""),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun PaymentMethodsEmptyPreview() {
    CarpoolTheme {
        PaymentMethodsContent(state = PaymentMethodsUiState(isLoading = false), onAction = {})
    }
}
