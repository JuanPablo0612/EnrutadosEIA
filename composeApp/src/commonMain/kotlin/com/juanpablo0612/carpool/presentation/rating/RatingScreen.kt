package com.juanpablo0612.carpool.presentation.rating

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juanpablo0612.carpool.domain.rating.model.RatingChip
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.rating_chip_amable
import enrutadoseia.composeapp.generated.resources.rating_chip_aporte_exacto
import enrutadoseia.composeapp.generated.resources.rating_chip_buen_viaje
import enrutadoseia.composeapp.generated.resources.rating_chip_buena_conversacion
import enrutadoseia.composeapp.generated.resources.rating_chip_carro_limpio
import enrutadoseia.composeapp.generated.resources.rating_chip_conduccion_segura
import enrutadoseia.composeapp.generated.resources.rating_chip_puntual
import enrutadoseia.composeapp.generated.resources.rating_chip_respetuoso
import enrutadoseia.composeapp.generated.resources.rating_comment_hint
import enrutadoseia.composeapp.generated.resources.rating_highlights
import enrutadoseia.composeapp.generated.resources.rating_skip
import enrutadoseia.composeapp.generated.resources.rating_submit
import enrutadoseia.composeapp.generated.resources.rating_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RatingScreen(
    viewModel: RatingViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RatingEvent.RatingSubmitted, RatingEvent.Skipped -> onDismiss()
        }
    }

    RatingContent(
        state = state,
        onAction = viewModel::onAction,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RatingContent(
    state: RatingUiState,
    onAction: (RatingAction) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler { /* Intercept back — non-dismissible */ }

    ModalBottomSheet(
        onDismissRequest = { /* Non-dismissible — user must tap skip or submit */ },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (state.rateeName.isBlank())
                    stringResource(Res.string.rating_title, "tu compañero/a de viaje")
                else
                    stringResource(Res.string.rating_title, state.rateeName),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            StarRow(
                selectedStars = state.selectedStars,
                onStarClick = { onAction(RatingAction.OnStarSelect(it)) }
            )

            if (state.availableChips.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.rating_highlights),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state.availableChips.forEach { chip ->
                            FilterChip(
                                selected = chip in state.selectedChips,
                                onClick = { onAction(RatingAction.OnChipToggle(chip)) },
                                label = { Text(chip.toLabel()) }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.comment,
                onValueChange = { onAction(RatingAction.OnCommentChange(it)) },
                placeholder = { Text(stringResource(Res.string.rating_comment_hint)) },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onAction(RatingAction.OnSubmit) },
                enabled = state.selectedStars > 0 && !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(Res.string.rating_submit))
                }
            }

            TextButton(
                onClick = { onAction(RatingAction.OnSkip) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.rating_skip))
            }
        }
    }
}

@Composable
private fun StarRow(
    selectedStars: Int,
    onStarClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            IconButton(onClick = { onStarClick(i) }) {
                Text(
                    text = if (i <= selectedStars) "★" else "☆",
                    fontSize = 36.sp,
                    color = if (i <= selectedStars) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun RatingChip.toLabel(): String = when (this) {
    is RatingChip.Puntual -> stringResource(Res.string.rating_chip_puntual)
    is RatingChip.Amable -> stringResource(Res.string.rating_chip_amable)
    is RatingChip.CarroLimpio -> stringResource(Res.string.rating_chip_carro_limpio)
    is RatingChip.ConduccionSegura -> stringResource(Res.string.rating_chip_conduccion_segura)
    is RatingChip.BuenaConversacion -> stringResource(Res.string.rating_chip_buena_conversacion)
    is RatingChip.Respetuoso -> stringResource(Res.string.rating_chip_respetuoso)
    is RatingChip.AporteExacto -> stringResource(Res.string.rating_chip_aporte_exacto)
    is RatingChip.BuenViaje -> stringResource(Res.string.rating_chip_buen_viaje)
}
