package com.juanpablo0612.carpool.presentation.payment.pay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.presentation.payment.pay.components.BreBKeyPayCard
import com.juanpablo0612.carpool.presentation.payment.pay.components.QrCodePayCard
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.formatPesos
import com.juanpablo0612.carpool.presentation.ui.util.plainTextClipEntry
import com.juanpablo0612.carpool.presentation.ui.util.rememberGalleryWriteAccess
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_back
import enrutadoseia.composeapp.generated.resources.error_payment_driver_load_failed
import enrutadoseia.composeapp.generated.resources.pay_driver_amount
import enrutadoseia.composeapp.generated.resources.pay_driver_done
import enrutadoseia.composeapp.generated.resources.pay_driver_empty_description
import enrutadoseia.composeapp.generated.resources.pay_driver_empty_title
import enrutadoseia.composeapp.generated.resources.pay_driver_free
import enrutadoseia.composeapp.generated.resources.pay_driver_title
import enrutadoseia.composeapp.generated.resources.pay_driver_to
import enrutadoseia.composeapp.generated.resources.payments_24px
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PayDriverScreen(
    viewModel: PayDriverViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PayDriverEvent.NavigateBack -> onBackClick()
        }
    }

    // Clipboard and gallery permission are platform APIs, so the screen handles them and reports back.
    val clipboard = LocalClipboard.current
    val galleryAccess = rememberGalleryWriteAccess()
    val scope = rememberCoroutineScope()
    PayDriverContent(
        state = state,
        onAction = viewModel::onAction,
        onCopyKey = { method ->
            scope.launch {
                clipboard.setClipEntry(plainTextClipEntry(method.key))
                viewModel.onAction(PayDriverAction.OnKeyCopied(method.id))
            }
        },
        onSaveQr = { method ->
            galleryAccess.request { granted ->
                viewModel.onAction(
                    if (granted) PayDriverAction.OnSaveQr(method) else PayDriverAction.OnGalleryPermissionDenied
                )
            }
        },
    )
}

@Composable
fun PayDriverContent(
    state: PayDriverUiState,
    onAction: (PayDriverAction) -> Unit,
    onCopyKey: (PaymentMethod.BreBKey) -> Unit,
    onSaveQr: (PaymentMethod.QrCode) -> Unit,
) {
    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.pay_driver_title),
                onBack = { onAction(PayDriverAction.OnBackClick) },
            )
        },
        bottomBar = {
            // Only leaves the screen: the app can't see the transfer, so it confirms nothing.
            if (!state.isLoading && !state.loadFailed && state.methods.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PrimaryButton(
                        text = stringResource(Res.string.pay_driver_done),
                        onClick = { onAction(PayDriverAction.OnBackClick) },
                        modifier = Modifier
                            .windowInsetsPadding(BottomBarInsets)
                            .centeredContent(ContentWidth.list)
                            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                            .fillMaxWidth(),
                    )
                }
            }
        },
    ) { padding ->
        val modifier = Modifier.fillMaxSize().padding(padding)
        when {
            state.isLoading -> DetailSkeleton(modifier = modifier)
            state.loadFailed -> ErrorState(
                description = stringResource(Res.string.error_payment_driver_load_failed),
                onRetry = { onAction(PayDriverAction.OnRetry) },
                modifier = modifier,
            )
            state.methods.isEmpty() -> EmptyState(
                icon = vectorResource(Res.drawable.payments_24px),
                title = stringResource(Res.string.pay_driver_empty_title),
                description = stringResource(Res.string.pay_driver_empty_description),
                primaryAction = ActionButton(stringResource(Res.string.cd_back)) {
                    onAction(PayDriverAction.OnBackClick)
                },
                modifier = modifier,
            )
            else -> Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .centeredContent(ContentWidth.list)
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                DriverAmountCard(driver = state.driver, contribution = state.contribution)
                state.error?.let { error ->
                    ErrorMessage(
                        message = stringResource(error.asStringResource()),
                        modifier = Modifier.fillMaxWidth().clickable { onAction(PayDriverAction.OnDismissError) },
                    )
                }
                state.methods.forEach { method ->
                    when (method) {
                        is PaymentMethod.BreBKey -> BreBKeyPayCard(
                            method = method,
                            isCopied = state.copiedMethodId == method.id,
                            onCopy = { onCopyKey(method) },
                        )
                        is PaymentMethod.QrCode -> QrCodePayCard(
                            method = method,
                            isSaving = state.savingQrId == method.id,
                            isSaved = state.savedQrId == method.id,
                            onSave = { onSaveQr(method) },
                        )
                    }
                }
            }
        }
    }
}

/** Who gets paid and how much, so the passenger types the right amount in the bank app. */
@Composable
private fun DriverAmountCard(driver: TripDriver, contribution: Int?) {
    val name = driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) }
    CarpoolListCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            UserAvatar(name = name, photoUrl = driver.photoUrl, size = 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.pay_driver_to, name),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (contribution != null && contribution > 0) {
                    Text(
                        text = stringResource(Res.string.pay_driver_amount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatPesos(contribution),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.pay_driver_free),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun PayDriverContentPreview() {
    CarpoolTheme {
        PayDriverContent(
            state = PayDriverUiState(
                isLoading = false,
                driver = TripDriver(name = "Carolina Restrepo"),
                contribution = 4_000,
                methods = listOf(
                    PaymentMethod.BreBKey(id = "1", key = "@carolina.r"),
                    PaymentMethod.QrCode(id = "2", imageUrl = ""),
                ),
                copiedMethodId = "1",
            ),
            onAction = {},
            onCopyKey = {},
            onSaveQr = {},
        )
    }
}
