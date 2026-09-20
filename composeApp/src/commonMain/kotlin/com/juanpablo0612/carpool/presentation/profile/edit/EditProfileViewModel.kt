package com.juanpablo0612.carpool.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.presentation.auth.toAuthError
import com.juanpablo0612.carpool.presentation.session.UserSession
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SAVED_BANNER_DURATION_MS = 900L

class EditProfileViewModel(
    private val userSession: UserSession,
    private val authRepository: AuthRepository
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
                    existingPhotoUrl = user?.photoUrl?.ifBlank { null },
                    isLoading = false
                )
            }
            _state.update { it.copy(initialSnapshot = it.snapshot) }
        }
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.OnNameChange -> _state.update {
                it.copy(name = action.name, nameError = null)
            }
            is EditProfileAction.OnPhoneChange -> _state.update { it.copy(phone = action.phone, phoneError = null) }
            is EditProfileAction.OnBioChange -> {
                val bio = action.bio
                val error = if (bio.length > 200) EditProfileFieldError.BioTooLong else null
                _state.update { it.copy(bio = bio, bioError = error) }
            }
            is EditProfileAction.OnPhotoSelected -> _state.update { it.copy(photoFile = action.file, photoError = false) }
            EditProfileAction.OnSaveClick -> save()
            EditProfileAction.OnBackClick -> {
                if (_state.value.isDirty) {
                    _state.update { it.copy(showDiscardConfirm = true) }
                } else {
                    viewModelScope.launch { _events.emit(EditProfileEvent.NavigateBack) }
                }
            }
            EditProfileAction.OnConfirmDiscard -> {
                _state.update { it.copy(showDiscardConfirm = false) }
                viewModelScope.launch { _events.emit(EditProfileEvent.NavigateBack) }
            }
            EditProfileAction.OnDismissDiscardConfirm -> _state.update { it.copy(showDiscardConfirm = false) }
        }
    }

    private fun save() {
        val state = _state.value
        if (state.name.isBlank()) {
            _state.update { it.copy(nameError = EditProfileFieldError.NameEmpty) }
            return
        }
        val phoneDigits = state.phone.filter { it.isDigit() }
        if (phoneDigits.isNotEmpty() && (phoneDigits.length != 10 || !phoneDigits.startsWith("3"))) {
            _state.update { it.copy(phoneError = EditProfileFieldError.PhoneInvalid) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, photoError = false) }
            val photoBytes = try {
                state.photoFile?.readBytes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, photoError = true) }
                return@launch
            }
            authRepository.updateProfile(
                name = state.name.trim(),
                phone = phoneDigits.ifBlank { null },
                bio = state.bio.trim().ifBlank { null },
                photoBytes = photoBytes
            ).fold(
                onSuccess = { updatedUser ->
                    userSession.setUser(updatedUser)
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                    delay(SAVED_BANNER_DURATION_MS)
                    _events.emit(EditProfileEvent.SaveSuccess)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isSaving = false, error = throwable.toAuthError()) }
                }
            )
        }
    }
}
