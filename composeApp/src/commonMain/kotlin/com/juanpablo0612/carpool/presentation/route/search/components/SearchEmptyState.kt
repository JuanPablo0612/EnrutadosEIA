package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.trip.model.SearchRelaxation
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesAction
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_empty_any_time_action
import enrutadoseia.composeapp.generated.resources.search_empty_any_time_hint
import enrutadoseia.composeapp.generated.resources.search_empty_subtitle
import enrutadoseia.composeapp.generated.resources.search_empty_title
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * No trips for this search. When trips exist at other times of day, offers to drop the time;
 * otherwise explains there is nothing yet, since direction, campus and place are the whole search.
 */
@Composable
internal fun SearchEmptyState(
    relaxation: SearchRelaxation?,
    onAction: (SearchRoutesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val anyTimeCount = relaxation?.anyTimeCount ?: 0
    if (anyTimeCount > 0) {
        EmptyState(
            icon = vectorResource(Res.drawable.search_24px),
            title = stringResource(Res.string.search_empty_title),
            description = pluralStringResource(Res.plurals.search_empty_any_time_hint, anyTimeCount, anyTimeCount),
            primaryAction = ActionButton(
                label = stringResource(Res.string.search_empty_any_time_action),
                onClick = { onAction(SearchRoutesAction.OnSearchAnyTime) },
            ),
            modifier = modifier,
        )
    } else {
        EmptyState(
            icon = vectorResource(Res.drawable.search_24px),
            title = stringResource(Res.string.search_empty_title),
            description = stringResource(Res.string.search_empty_subtitle),
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun SearchEmptyStateAnyTimePreview() {
    CarpoolTheme {
        SearchEmptyState(
            relaxation = SearchRelaxation(anyTimeCount = 2),
            onAction = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@Preview
@Composable
private fun SearchEmptyStateNothingPreview() {
    CarpoolTheme {
        SearchEmptyState(relaxation = null, onAction = {}, modifier = Modifier.padding(Spacing.lg))
    }
}
