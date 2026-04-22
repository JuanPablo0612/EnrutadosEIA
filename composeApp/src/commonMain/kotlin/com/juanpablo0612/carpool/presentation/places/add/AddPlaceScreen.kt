package com.juanpablo0612.carpool.presentation.places.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.AuthTextField
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_new_place_title
import enrutadoseia.composeapp.generated.resources.place_address_label
import enrutadoseia.composeapp.generated.resources.place_name_label
import enrutadoseia.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddPlaceScreen(
    viewModel: AddPlaceViewModel,
    onBack: () -> Unit,
    onPlaceSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddPlaceEvent.PlaceSaved -> onPlaceSaved()
            AddPlaceEvent.NavigateBack -> onBack()
        }
    }

    AddPlaceContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun AddPlaceContent(
    state: AddPlaceUiState,
    onAction: (AddPlaceAction) -> Unit
) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            AuthTextField(
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

            AuthTextField(
                value = state.address,
                onValueChange = { onAction(AddPlaceAction.OnAddressChanged(it)) },
                label = stringResource(Res.string.place_address_label),
                placeholder = stringResource(Res.string.place_address_label),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                errorMessage = state.addressError?.asStringResource()?.let { stringResource(it) }
            )

            state.generalError?.let {
                ErrorMessage(message = stringResource(it.asStringResource()))
            }

            PrimaryButton(
                text = stringResource(Res.string.save_button),
                onClick = { onAction(AddPlaceAction.OnSaveClick) },
                isLoading = state.isLoading
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
