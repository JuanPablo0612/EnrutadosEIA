package com.juanpablo0612.carpool.presentation.route.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailAction
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.publish_week_title
import enrutadoseia.composeapp.generated.resources.route_duplicate_button
import enrutadoseia.composeapp.generated.resources.route_publish_trip_button
import enrutadoseia.composeapp.generated.resources.route_save_changes_button
import org.jetbrains.compose.resources.stringResource

/** What the driver can do with the saved route: publish trips from it or copy it. */
@Composable
internal fun RouteReadActions(
    route: Route,
    isDuplicating: Boolean,
    onAction: (RouteDetailAction) -> Unit,
) {
    BottomActions {
        Button(
            onClick = { onAction(RouteDetailAction.OnPublishTripClick) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.route_publish_trip_button))
        }
        if (route.recurringDays.isNotEmpty() && route.typicalDepartureTime != null) {
            OutlinedButton(
                onClick = { onAction(RouteDetailAction.OnPublishWeekClick) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.publish_week_title))
            }
        }
        TextButton(
            onClick = { onAction(RouteDetailAction.OnDuplicateClick) },
            enabled = !isDuplicating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isDuplicating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(Res.string.route_duplicate_button))
            }
        }
    }
}

/** Saving edits stays pinned below the form, so it's reachable however long the stop list gets. */
@Composable
internal fun RouteEditActions(
    canSave: Boolean,
    isSaving: Boolean,
    onAction: (RouteDetailAction) -> Unit,
) {
    BottomActions {
        Button(
            onClick = { onAction(RouteDetailAction.OnSaveChangesClick) },
            enabled = canSave && !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(Res.string.route_save_changes_button))
            }
        }
    }
}

@Composable
private fun BottomActions(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(BottomBarInsets)
            .centeredContent(ContentWidth.form)
            .padding(horizontal = Spacing.screenHorizontal)
            .padding(top = Spacing.sm, bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        content()
    }
}
