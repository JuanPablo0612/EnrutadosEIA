package com.juanpablo0612.carpool.presentation.places.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.PlaceType
import com.juanpablo0612.carpool.presentation.places.add.components.MapPreview
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_new_place_title
import enrutadoseia.composeapp.generated.resources.add_place_address_hint
import enrutadoseia.composeapp.generated.resources.add_place_map_tip
import enrutadoseia.composeapp.generated.resources.add_place_pick_on_map
import enrutadoseia.composeapp.generated.resources.add_place_type_label
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.place_name_label
import enrutadoseia.composeapp.generated.resources.place_type_gym
import enrutadoseia.composeapp.generated.resources.place_type_home
import enrutadoseia.composeapp.generated.resources.place_type_other
import enrutadoseia.composeapp.generated.resources.place_type_university
import enrutadoseia.composeapp.generated.resources.place_type_work
import enrutadoseia.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AddPlaceScreen(
    viewModel: AddPlaceViewModel,
    onBack: () -> Unit,
    onPlaceSaved: () -> Unit,
    onNavigateToMapPicker: (Double?, Double?) -> Unit = { _, _ -> },
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddPlaceEvent.PlaceSaved -> onPlaceSaved()
            AddPlaceEvent.NavigateBack -> onBack()
            AddPlaceEvent.NavigateToMapPicker -> onNavigateToMapPicker(
                state.coordinates?.latitude,
                state.coordinates?.longitude,
            )
        }
    }

    AddPlaceContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPlaceContent(
    state: AddPlaceUiState,
    onAction: (AddPlaceAction) -> Unit
) {
    val placeTypes = listOf(
        PlaceType.Home to stringResource(Res.string.place_type_home),
        PlaceType.Work to stringResource(Res.string.place_type_work),
        PlaceType.Gym to stringResource(Res.string.place_type_gym),
        PlaceType.University to stringResource(Res.string.place_type_university),
        PlaceType.Other to stringResource(Res.string.place_type_other),
    )

    Scaffold(
        topBar = {
            AuthTopBar(
                title = stringResource(Res.string.add_new_place_title),
                onBackClick = { onAction(AddPlaceAction.OnBackClick) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.screenHorizontalForm)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = stringResource(Res.string.add_place_type_label),
                style = MaterialTheme.typography.labelLarge,
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                placeTypes.forEach { (type, label) ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onAction(AddPlaceAction.SelectType(type)) },
                        label = { Text(label) },
                    )
                }
            }

            CarpoolTextField(
                value = state.name,
                onValueChange = { onAction(AddPlaceAction.OnNameChanged(it)) },
                label = stringResource(Res.string.place_name_label),
                placeholder = stringResource(Res.string.place_name_label),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                errorMessage = state.nameError?.asStringResource()?.let { stringResource(it) }
            )

            // Address field with loading spinner
            CarpoolTextField(
                value = state.address,
                onValueChange = { onAction(AddPlaceAction.OnAddressChanged(it)) },
                label = stringResource(Res.string.add_place_address_hint),
                placeholder = stringResource(Res.string.add_place_address_hint),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = if (state.isSearchingAddress) {
                    { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                } else null,
                errorMessage = null,
            )

            // Inline autocomplete suggestions
            AnimatedVisibility(visible = state.autocompleteSuggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.raised),
                ) {
                    Column {
                        state.autocompleteSuggestions.forEachIndexed { index, suggestion ->
                            ListItem(
                                headlineContent = {
                                    Text(suggestion.primaryText, style = MaterialTheme.typography.bodyMedium)
                                },
                                supportingContent = {
                                    Text(
                                        suggestion.fullAddress,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onAction(AddPlaceAction.SelectSuggestion(suggestion))
                                },
                            )
                            if (index < state.autocompleteSuggestions.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }

            // "Pick on map" button — visible when no suggestions are showing
            if (state.autocompleteSuggestions.isEmpty()) {
                OutlinedButton(
                    onClick = { onAction(AddPlaceAction.PickOnMap) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.location_on_24px),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp), // icon-intrinsic size
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(stringResource(Res.string.add_place_pick_on_map))
                }
            }

            if (state.coordinates != null) {
                MapPreview(
                    coordinates = state.coordinates,
                    onPinDragged = { onAction(AddPlaceAction.DragPin(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // map preview intrinsic height
                )

                Text(
                    text = stringResource(Res.string.add_place_map_tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            state.generalError?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
            }

            PrimaryButton(
                text = stringResource(Res.string.save_button),
                onClick = { onAction(AddPlaceAction.OnSaveClick) },
                isLoading = state.isSaving,
                enabled = state.isValid,
            )

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview
@Composable
private fun AddPlacePreview() {
    CarpoolTheme {
        AddPlaceContent(
            state = AddPlaceUiState(),
            onAction = {}
        )
    }
}
