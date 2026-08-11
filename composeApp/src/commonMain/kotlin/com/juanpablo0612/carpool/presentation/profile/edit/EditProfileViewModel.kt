package com.juanpablo0612.carpool.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.UpdateProfileUseCase
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userSession: UserSession,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events: SharedFlow<EditProfileEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val user = userSession.user.first()
            _state.update {
                it.copy(
                    name = user?.name ?: "",
                    phone = user?.phone ?: "",
                    bio = user?.bio ?: "",
                    isLoading = false
                )
            }
        }
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.OnNameChange -> _state.update {
                it.copy(name = action.name, nameError = null)
            }
            is EditProfileAction.OnPhoneChange -> _state.update { it.copy(phone = action.phone) }
            is EditProfileAction.OnBioChange -> {
                val bio = action.bio
                val error = if (bio.length > 200) EditProfileFieldError.BioTooLong else null
                _state.update { it.copy(bio = bio, bioError = error) }
            }
            EditProfileAction.OnSaveClick -> save()
        }
    }

    private fun save() {
        val state = _state.value
        if (state.name.isBlank()) {
            _state.update { it.copy(nameError = EditProfileFieldError.NameEmpty) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            updateProfileUseCase(
                name = state.name.trim(),
                phone = state.phone.trim().ifBlank { null },
                bio = state.bio.trim().ifBlank { null }
            ).fold(
                onSuccess = { updatedUser ->
                    userSession.setUser(updatedUser)
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(EditProfileEvent.SaveSuccess)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isSaving = false, error = throwable.toAuthError()) }
                }
            )
        }
    }
}
