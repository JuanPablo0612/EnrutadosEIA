package com.juanpablo0612.carpool.presentation.route.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.juanpablo0612.carpool.presentation.place.stops.stopsEditorItems
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailAction
import com.juanpablo0612.carpool.presentation.ui.components.AdaptiveTimePicker
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DaySelector
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.departure_time_label
import enrutadoseia.composeapp.generated.resources.departure_time_not_set
import enrutadoseia.composeapp.generated.resources.discard_changes_body
import enrutadoseia.composeapp.generated.resources.discard_changes_confirm
import enrutadoseia.composeapp.generated.resources.discard_changes_title
import enrutadoseia.composeapp.generated.resources.notice_route_updated
import enrutadoseia.composeapp.generated.resources.recurrence_section_title
import enrutadoseia.composeapp.generated.resources.route_name_label
import enrutadoseia.composeapp.generated.resources.route_name_placeholder
import enrutadoseia.composeapp.generated.resources.waypoints_section_title
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/** The route's fields as an in-place form, below the same map the read mode shows. */
internal fun LazyListScope.routeEditItems(
    draft: CreateRouteUiState,
    isSaved: Boolean,
    onAction: (RouteDetailAction) -> Unit,
    onPickTime: () -> Unit,
) {
    item(key = "edit_name") {
        OutlinedTextField(
            value = draft.name,
            onValueChange = { onAction(RouteDetailAction.OnNameChange(it)) },
            label = { Text(stringResource(Res.string.route_name_label)) },
            placeholder = { Text(stringResource(Res.string.route_name_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
            singleLine = true
        )
    }

    item(key = "edit_stops_header") { SectionHeader(stringResource(Res.string.waypoints_section_title)) }

    stopsEditorItems(
        stops = draft.stops,
        onOriginClick = { onAction(RouteDetailAction.OnOriginClick) },
        onDestinationClick = { onAction(RouteDetailAction.OnDestinationClick) },
        onEditWaypoint = { onAction(RouteDetailAction.OnEditWaypointClick(it)) },
        onRemoveWaypoint = { onAction(RouteDetailAction.OnRemoveWaypoint(it)) },
        onAddWaypoint = { onAction(RouteDetailAction.OnAddWaypointClick) },
    )

    item(key = "edit_recurrence_header") { SectionHeader(stringResource(Res.string.recurrence_section_title)) }

    item(key = "edit_days") {
        DaySelector(
            selectedDays = draft.recurringDays,
            onToggleDay = { onAction(RouteDetailAction.OnToggleRecurringDay(it)) },
            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
        )
    }

    item(key = "edit_time") {
        val timeLabel = draft.typicalDepartureTime?.let { t ->
            stringResource(
                Res.string.departure_time_label,
                "${t.hour.toString().padStart(2, '0')}:${t.minute.toString().padStart(2, '0')}"
            )
        } ?: stringResource(Res.string.departure_time_not_set)
        TextButton(onClick = onPickTime, modifier = Modifier.padding(horizontal = Spacing.sm)) {
            Text(timeLabel)
        }
    }

    if (isSaved) {
        item(key = "edit_saved") {
            SuccessMessage(
                message = stringResource(Res.string.notice_route_updated),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
            )
        }
    }
}

/** The dialogs edit mode can raise: the departure-time picker and the discard-changes check. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RouteEditDialogs(
    draft: CreateRouteUiState,
    showTimePicker: Boolean,
    showDiscardConfirm: Boolean,
    onDismissTimePicker: () -> Unit,
    onAction: (RouteDetailAction) -> Unit,
) {
    if (showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(RouteDetailAction.OnConfirmDiscardEdit) },
            onDismiss = { onAction(RouteDetailAction.OnDismissDiscardEditConfirm) },
            isDestructive = true
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = draft.typicalDepartureTime?.hour ?: 7,
            initialMinute = draft.typicalDepartureTime?.minute ?: 0
        )
        TimePickerDialog(
            onCancel = onDismissTimePicker,
            onConfirm = {
                onAction(RouteDetailAction.OnSetDepartureTime(LocalTime(timePickerState.hour, timePickerState.minute)))
                onDismissTimePicker()
            }
        ) {
            AdaptiveTimePicker(state = timePickerState)
        }
    }
}
