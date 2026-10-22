package com.juanpablo0612.carpool.presentation.payment.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val COPIED_CONFIRMATION_MS = 2_000L

/** How to pay the driver of [tripId]: the trip gives the driver and amount, their profile the methods. */
class PayDriverViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PayDriverUiState())
    val state: StateFlow<PayDriverUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PayDriverEvent>()
    val events: SharedFlow<PayDriverEvent> = _events.asSharedFlow()

    private var copiedJob: Job? = null

    init {
        load()
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId).getOrNull()
            val methods = trip?.let { paymentMethodRepository.getPaymentMethods(it.driverId).getOrNull() }
            _state.update {
                if (trip == null || methods == null) {
                    it.copy(isLoading = false, loadFailed = true)
                } else {
                    it.copy(
                        isLoading = false,
                        driver = trip.driver,
                        contribution = trip.contributionPerPassenger,
                        methods = methods,
                    )
                }
            }
        }
    }

    fun onAction(action: PayDriverAction) {
        when (action) {
            is PayDriverAction.OnKeyCopied -> {
                _state.update { it.copy(copiedMethodId = action.methodId) }
                copiedJob?.cancel()
                copiedJob = viewModelScope.launch {
                    delay(COPIED_CONFIRMATION_MS)
                    _state.update { it.copy(copiedMethodId = null) }
                }
            }
            is PayDriverAction.OnSaveQr -> saveQr(action.method)
            PayDriverAction.OnGalleryPermissionDenied ->
                _state.update { it.copy(savedQrId = null, error = PayDriverError.GalleryPermissionDenied) }
            PayDriverAction.OnDismissError -> _state.update { it.copy(error = null) }
            PayDriverAction.OnRetry -> load()
            PayDriverAction.OnBackClick -> viewModelScope.launch { _events.emit(PayDriverEvent.NavigateBack) }
        }
    }

    private fun saveQr(method: PaymentMethod.QrCode) {
        if (_state.value.savingQrId != null) return
        _state.update { it.copy(savingQrId = method.id, savedQrId = null, error = null) }
        viewModelScope.launch {
            paymentMethodRepository.saveQrCodeToGallery(method, fileName = "enrutados_qr_${method.id}.jpg")
                .onSuccess { _state.update { it.copy(savingQrId = null, savedQrId = method.id) } }
                .onFailure { _state.update { it.copy(savingQrId = null, error = PayDriverError.SaveQrFailed) } }
        }
    }
}
