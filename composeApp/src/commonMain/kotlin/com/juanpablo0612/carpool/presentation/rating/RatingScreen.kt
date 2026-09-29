package com.juanpablo0612.carpool.presentation.rating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.rating.model.RatingChip
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_rating_star
import enrutadoseia.composeapp.generated.resources.error_already_rated
import enrutadoseia.composeapp.generated.resources.label_pair
import enrutadoseia.composeapp.generated.resources.rating_already_rated_body
import enrutadoseia.composeapp.generated.resources.rating_back
import enrutadoseia.composeapp.generated.resources.rating_chip_amable
import enrutadoseia.composeapp.generated.resources.rating_chip_aporte_exacto
import enrutadoseia.composeapp.generated.resources.rating_chip_buen_viaje
import enrutadoseia.composeapp.generated.resources.rating_chip_buena_conversacion
import enrutadoseia.composeapp.generated.resources.rating_chip_carro_limpio
import enrutadoseia.composeapp.generated.resources.rating_chip_conduccion_segura
import enrutadoseia.composeapp.generated.resources.rating_chip_puntual
import enrutadoseia.composeapp.generated.resources.rating_chip_respetuoso
import enrutadoseia.composeapp.generated.resources.rating_comment_counter
import enrutadoseia.composeapp.generated.resources.rating_comment_hint
import enrutadoseia.composeapp.generated.resources.rating_comment_placeholder
import enrutadoseia.composeapp.generated.resources.rating_default_ratee_name
import enrutadoseia.composeapp.generated.resources.rating_highlights
import enrutadoseia.composeapp.generated.resources.rating_highlights_hint
import enrutadoseia.composeapp.generated.resources.rating_skip
import enrutadoseia.composeapp.generated.resources.rating_stars_1
import enrutadoseia.composeapp.generated.resources.rating_stars_2
import enrutadoseia.composeapp.generated.resources.rating_stars_3
import enrutadoseia.composeapp.generated.resources.rating_stars_4
import enrutadoseia.composeapp.generated.resources.rating_stars_5
import enrutadoseia.composeapp.generated.resources.rating_stars_prompt
import enrutadoseia.composeapp.generated.resources.rating_submit
import enrutadoseia.composeapp.generated.resources.rating_title
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.route_from_to
import enrutadoseia.composeapp.generated.resources.star_24px
import enrutadoseia.composeapp.generated.resources.star_outline_24px
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RatingScreen(
    viewModel: RatingViewModel,
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RatingEvent.RatingSubmitted, RatingEvent.Skipped -> onDismiss()
        }
    }

    RatingContent(state = state, onAction = viewModel::onAction)
}

/**
 * Rating the other person after a trip, as its own screen: who and which trip on top, then the
 * stars (the only required answer), optional highlights and an optional comment. "Ahora no" and
 * system back both leave without rating; it can be done later from Mis viajes.
 */
@Composable
fun RatingContent(
    state: RatingUiState,
    onAction: (RatingAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.End,
            ) {
                if (!state.alreadyRated) {
                    TextButton(onClick = { onAction(RatingAction.OnSkip) }) {
                        Text(stringResource(Res.string.rating_skip), style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        },
        bottomBar = {
            if (!state.isLoading && !state.alreadyRated) {
                PrimaryButton(
                    text = stringResource(Res.string.rating_submit),
                    onClick = { onAction(RatingAction.OnSubmit) },
                    enabled = state.selectedStars > 0 && state.error != RatingError.AlreadyRated,
                    isLoading = state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val modifier = Modifier.fillMaxSize().padding(padding)
        when {
            state.isLoading -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.alreadyRated -> EmptyState(
                icon = vectorResource(Res.drawable.star_24px),
                title = stringResource(Res.string.error_already_rated),
                description = stringResource(Res.string.rating_already_rated_body),
                primaryAction = ActionButton(stringResource(Res.string.rating_back)) { onAction(RatingAction.OnSkip) },
                modifier = modifier,
            )
            else -> RatingForm(state = state, onAction = onAction, modifier = modifier)
        }
    }
}

@Composable
private fun RatingForm(state: RatingUiState, onAction: (RatingAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl, vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        RateeHeader(target = state.target)
        StarPicker(selectedStars = state.selectedStars, onSelect = { onAction(RatingAction.OnStarSelect(it)) })
        if (state.availableChips.isNotEmpty()) {
            Highlights(chips = state.availableChips, selected = state.selectedChips, onAction = onAction)
        }
        CarpoolTextField(
            value = state.comment,
            onValueChange = { onAction(RatingAction.OnCommentChange(it)) },
            label = stringResource(Res.string.rating_comment_hint),
            placeholder = stringResource(Res.string.rating_comment_placeholder),
            singleLine = false,
            minLines = 3,
            supportingText = {
                Text(
                    stringResource(
                        Res.string.rating_comment_counter,
                        state.comment.length,
                        RatingViewModel.MAX_COMMENT_LENGTH,
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
        )
        state.error?.let { ErrorMessage(message = stringResource(it.asStringResource())) }
    }
}

@Composable
private fun RateeHeader(target: RatingTarget) {
    val name = target.rateeName.ifBlank { stringResource(Res.string.rating_default_ratee_name) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        UserAvatar(name = target.rateeName, photoUrl = target.rateePhotoUrl, size = 80.dp)
        Text(
            text = stringResource(Res.string.rating_title, name.substringBefore(' ')),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        tripLine(target)?.let { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** "Hoy · 6:30 p. m. · Envigado → Las Palmas", with whatever parts the opener passed. */
@Composable
private fun tripLine(target: RatingTarget): String? {
    val now = rememberNowMs()
    val time = target.departureTime?.let {
        stringResource(
            Res.string.relative_day_at_time,
            departureDayLabel(it, now).replaceFirstChar { c -> c.uppercaseChar() },
            formatTime(it),
        )
    }
    val route = if (target.originName.isNotBlank() && target.destinationName.isNotBlank()) {
        stringResource(Res.string.route_from_to, target.originName, target.destinationName)
    } else {
        null
    }
    return when {
        time != null && route != null -> stringResource(Res.string.label_pair, time, route)
        else -> time ?: route
    }
}

@Composable
private fun StarPicker(selectedStars: Int, onSelect: (Int) -> Unit) {
    val ratingColor = LocalExtendedColors.current.rating
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(modifier = Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            for (stars in 1..5) {
                val filled = stars <= selectedStars
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .selectable(selected = stars == selectedStars, role = Role.RadioButton, onClick = { onSelect(stars) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(if (filled) Res.drawable.star_24px else Res.drawable.star_outline_24px),
                        contentDescription = stringResource(Res.string.cd_rating_star, stars),
                        tint = if (filled) ratingColor else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
        Text(
            text = stringResource(starLabel(selectedStars)),
            style = if (selectedStars > 0) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (selectedStars > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

private fun starLabel(stars: Int): StringResource = when (stars) {
    1 -> Res.string.rating_stars_1
    2 -> Res.string.rating_stars_2
    3 -> Res.string.rating_stars_3
    4 -> Res.string.rating_stars_4
    5 -> Res.string.rating_stars_5
    else -> Res.string.rating_stars_prompt
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Highlights(chips: List<RatingChip>, selected: Set<RatingChip>, onAction: (RatingAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = stringResource(Res.string.rating_highlights),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(Res.string.rating_highlights_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            chips.forEach { chip ->
                FilterChip(
                    selected = chip in selected,
                    onClick = { onAction(RatingAction.OnChipToggle(chip)) },
                    label = { Text(chip.toLabel()) },
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

@Preview
@Composable
private fun RatingContentPreview() {
    CarpoolTheme {
        RatingContent(
            state = RatingUiState(
                target = RatingTarget(
                    bookingId = "b1",
                    tripId = "t1",
                    rateeId = "d1",
                    rateeName = "Carolina Restrepo",
                    rateeIsDriver = true,
                    originName = "Envigado",
                    destinationName = "Las Palmas",
                ),
                selectedStars = 4,
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
