package com.juanpablo0612.carpool.presentation.profile.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.auth.common.asStringResource
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_counter
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_label
import enrutadoseia.composeapp.generated.resources.edit_profile_bio_placeholder
import enrutadoseia.composeapp.generated.resources.edit_profile_name_label
import enrutadoseia.composeapp.generated.resources.edit_profile_phone_label
import enrutadoseia.composeapp.generated.resources.edit_profile_phone_placeholder
import enrutadoseia.composeapp.generated.resources.edit_profile_save_button
import enrutadoseia.composeapp.generated.resources.edit_profile_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EditProfileEvent.SaveSuccess -> onSaved()
        }
    }

    EditProfileContent(state = state, onAction = viewModel::onAction, onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    state: EditProfileUiState,
    onAction: (EditProfileAction) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.edit_profile_title),
                onBack = onBackClick,
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(EditProfileAction.OnNameChange(it)) },
                    label = { Text(stringResource(Res.string.edit_profile_name_label)) },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(stringResource(it.asStringResource())) } },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { onAction(EditProfileAction.OnPhoneChange(it)) },
                    label = { Text(stringResource(Res.string.edit_profile_phone_label)) },
                    placeholder = { Text(stringResource(Res.string.edit_profile_phone_placeholder)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.bio,
                    onValueChange = { onAction(EditProfileAction.OnBioChange(it)) },
                    label = { Text(stringResource(Res.string.edit_profile_bio_label)) },
                    placeholder = { Text(stringResource(Res.string.edit_profile_bio_placeholder)) },
                    isError = state.bioError != null,
                    supportingText = {
                        Text(
                            text = state.bioError?.let { stringResource(it.asStringResource()) }
                                ?: stringResource(Res.string.edit_profile_bio_counter, state.bio.length),
                            color = if (state.bioError != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                state.error?.let {
                    ErrorMessage(message = stringResource(it.asStringResource()))
                }

                Button(
                    onClick = { onAction(EditProfileAction.OnSaveClick) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                    else Text(stringResource(Res.string.edit_profile_save_button))
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
