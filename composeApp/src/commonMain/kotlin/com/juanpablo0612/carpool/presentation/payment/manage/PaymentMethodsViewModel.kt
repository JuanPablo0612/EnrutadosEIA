package com.juanpablo0612.carpool.presentation.payment.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.payment.usecase.AddPaymentMethodUseCase
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidationError
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentMethodsViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val addPaymentMethodUseCase: AddPaymentMethodUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodsUiState())
    val state: StateFlow<PaymentMethodsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PaymentMethodsEvent>()
    val events: SharedFlow<PaymentMethodsEvent> = _events.asSharedFlow()

    private var observeJob: Job? = null

    init {
        observeMethods()
    }

    private fun observeMethods() {
        val userId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false, loadFailed = true) }
            return
        }
        observeJob?.cancel()
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        observeJob = paymentMethodRepository.observePaymentMethods(userId)
            .onEach { methods -> _state.update { it.copy(isLoading = false, methods = methods) } }
            .catch { _state.update { it.copy(isLoading = false, loadFailed = true) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: PaymentMethodsAction) {
        when (action) {
            PaymentMethodsAction.OnAddClick -> _state.update {
                if (it.canAddMore) it.copy(showTypePicker = true, error = null)
                else it.copy(error = PaymentMethodsError.LimitReached)
            }
            PaymentMethodsAction.OnDismissTypePicker -> _state.update { it.copy(showTypePicker = false) }
            is PaymentMethodsAction.OnTypeSelected -> _state.update {
                it.copy(showTypePicker = false, editor = emptyEditor(action.type))
            }
            is PaymentMethodsAction.OnBreBKeyChange -> updateEditor<PaymentMethodEditor.BreBKey> {
                it.copy(key = action.key, fieldError = null)
            }
            is PaymentMethodsAction.OnQrImagePicked -> onQrImagePicked(action)
            PaymentMethodsAction.OnSave -> save()
            PaymentMethodsAction.OnDismissEditor -> _state.update {
                if (it.isSaving) it else it.copy(editor = null, error = null)
            }
            is PaymentMethodsAction.OnDeleteRequest -> _state.update { it.copy(methodToDelete = action.method) }
            PaymentMethodsAction.OnDismissDelete -> _state.update { it.copy(methodToDelete = null) }
            PaymentMethodsAction.OnConfirmDelete -> delete()
            PaymentMethodsAction.OnDismissError -> _state.update { it.copy(error = null) }
            PaymentMethodsAction.OnRetry -> observeMethods()
            PaymentMethodsAction.OnBackClick -> viewModelScope.launch { _events.emit(PaymentMethodsEvent.NavigateBack) }
        }
    }

    private fun emptyEditor(type: PaymentMethodType): PaymentMethodEditor = when (type) {
        PaymentMethodType.BreBKey -> PaymentMethodEditor.BreBKey()
        PaymentMethodType.QrCode -> PaymentMethodEditor.QrCode()
    }

    private fun onQrImagePicked(action: PaymentMethodsAction.OnQrImagePicked) {
        // Closing the gallery or camera without choosing leaves the form as it was.
        val file = action.file ?: return
        updateEditor<PaymentMethodEditor.QrCode> { it.copy(imageFile = file, fieldError = null) }
    }

    private inline fun <reified E : PaymentMethodEditor> updateEditor(crossinline transform: (E) -> E) {
        _state.update { state ->
            val editor = state.editor as? E ?: return@update state
            // Any edit answers the last save error, so it stops showing.
            state.copy(editor = transform(editor), error = null)
        }
    }

    private fun save() {
        val editor = _state.value.editor ?: return
        if (_state.value.isSaving) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val draft = when (editor) {
                is PaymentMethodEditor.BreBKey -> PaymentMethodDraft.BreBKey(editor.key)
                is PaymentMethodEditor.QrCode -> PaymentMethodDraft.QrCode(editor.imageFile?.readBytes() ?: ByteArray(0))
            }
            addPaymentMethodUseCase(draft)
                .onSuccess { _state.update { it.copy(isSaving = false, editor = null) } }
                .onFailure { e -> onSaveFailed(e) }
        }
    }

    private fun onSaveFailed(e: Throwable) {
        _state.update { state ->
            val saving = state.copy(isSaving = false)
            when (e) {
                is AppException.PaymentException.Invalid -> saving.withFieldError(e.errors.first())
                AppException.PaymentException.LimitReached -> saving.copy(editor = null, error = PaymentMethodsError.LimitReached)
                AppException.PaymentException.AlreadyAdded -> saving.copy(error = PaymentMethodsError.AlreadyAdded)
                else -> saving.copy(error = PaymentMethodsError.SaveFailed)
            }
        }
    }

    private fun PaymentMethodsUiState.withFieldError(error: PaymentMethodValidationError) = copy(
        editor = when (val editor = editor) {
            is PaymentMethodEditor.BreBKey -> editor.copy(fieldError = error)
            is PaymentMethodEditor.QrCode -> editor.copy(fieldError = error)
            null -> null
        }
    )

    private fun delete() {
        val method = _state.value.methodToDelete ?: return
        val userId = authRepository.getCurrentUserId() ?: return
        _state.update { it.copy(methodToDelete = null) }
        viewModelScope.launch {
            paymentMethodRepository.deletePaymentMethod(userId, method)
                .onFailure { _state.update { it.copy(error = PaymentMethodsError.DeleteFailed) } }
        }
    }
}
