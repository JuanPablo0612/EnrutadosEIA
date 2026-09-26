package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.trip.model.SearchRelaxation
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesAction
import com.juanpablo0612.carpool.presentation.route.search.formatDistance
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_adjust_button
import enrutadoseia.composeapp.generated.resources.search_empty_any_time_action
import enrutadoseia.composeapp.generated.resources.search_empty_any_time_hint
import enrutadoseia.composeapp.generated.resources.search_empty_nearby_title
import enrutadoseia.composeapp.generated.resources.search_empty_subtitle
import enrutadoseia.composeapp.generated.resources.search_empty_title
import enrutadoseia.composeapp.generated.resources.search_empty_widen_radius_action
import enrutadoseia.composeapp.generated.resources.search_empty_widen_radius_hint
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Empty search results that point at a concrete way out: a wider walking radius that would find
 * trips, or dropping the time constraint. Falls back to the generic "adjust filters" state.
 */
@Composable
internal fun SearchEmptyState(
    relaxation: SearchRelaxation?,
    onAction: (SearchRoutesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val widerRadius = relaxation?.widerRadiusMeters
    val anyTimeCount = relaxation?.anyTimeCount ?: 0
    val anyTimeAction = if (anyTimeCount > 0) {
        ActionButton(
            label = stringResource(Res.string.search_empty_any_time_action),
            onClick = { onAction(SearchRoutesAction.OnSearchAnyTime) }
        )
    } else null

    when {
        widerRadius != null -> {
            val distance = formatDistance(widerRadius.toDouble())
            EmptyState(
                icon = vectorResource(Res.drawable.search_24px),
                title = stringResource(Res.string.search_empty_nearby_title),
                description = pluralStringResource(
                    Res.plurals.search_empty_widen_radius_hint,
                    relaxation.widerRadiusCount,
                    relaxation.widerRadiusCount,
                    distance
                ),
                primaryAction = ActionButton(
                    label = stringResource(Res.string.search_empty_widen_radius_action, distance),
                    onClick = { onAction(SearchRoutesAction.OnWidenRadius(widerRadius)) }
                ),
                secondaryAction = anyTimeAction,
                modifier = modifier,
            )
        }

        anyTimeAction != null -> EmptyState(
            icon = vectorResource(Res.drawable.search_24px),
            title = stringResource(Res.string.search_empty_title),
            description = pluralStringResource(Res.plurals.search_empty_any_time_hint, anyTimeCount, anyTimeCount),
            primaryAction = anyTimeAction,
            modifier = modifier,
        )

        else -> EmptyState(
            icon = vectorResource(Res.drawable.search_24px),
            title = stringResource(Res.string.search_empty_title),
            description = stringResource(Res.string.search_empty_subtitle),
            primaryAction = ActionButton(
                label = stringResource(Res.string.search_adjust_button),
                onClick = { onAction(SearchRoutesAction.OnShowFilters) }
            ),
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun SearchEmptyStateWidenPreview() {
    CarpoolTheme {
        SearchEmptyState(
            relaxation = SearchRelaxation(widerRadiusMeters = 2_000, widerRadiusCount = 3, anyTimeCount = 1),
            onAction = {}
        )
    }
}
