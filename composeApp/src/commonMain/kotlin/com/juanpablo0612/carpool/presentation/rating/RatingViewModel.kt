package com.juanpablo0612.carpool.presentation.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.rating.model.RatingChip
import com.juanpablo0612.carpool.domain.rating.use_case.CreateRatingUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RatingViewModel(
    private val bookingId: String,
    private val tripId: String,
    private val rateeId: String,
    private val rateeName: String,
    private val isDriver: Boolean,
    private val createRatingUseCase: CreateRatingUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        RatingUiState(
            bookingId = bookingId,
            rateeId = rateeId,
            rateeName = rateeName,
            isDriver = isDriver
        )
    )
    val state: StateFlow<RatingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RatingEvent>()
    val events: SharedFlow<RatingEvent> = _events.asSharedFlow()

    fun onAction(action: RatingAction) {
        when (action) {
            is RatingAction.OnStarSelect -> _state.update { it.copy(selectedStars = action.stars) }
            is RatingAction.OnChipToggle -> {
                val chips = _state.value.selectedChips.toMutableSet()
                if (action.chip in chips) chips.remove(action.chip) else chips.add(action.chip)
                _state.update { it.copy(selectedChips = chips) }
            }
            is RatingAction.OnCommentChange -> _state.update { it.copy(comment = action.comment) }
            RatingAction.OnSubmit -> submit()
            RatingAction.OnSkip -> viewModelScope.launch { _events.emit(RatingEvent.Skipped) }
        }
    }

    private fun submit() {
        val state = _state.value
        if (state.selectedStars == 0) return
        val raterId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            createRatingUseCase(
                tripId = tripId,
                bookingId = bookingId,
                raterId = raterId,
                rateeId = rateeId,
                stars = state.selectedStars,
                chips = state.selectedChips.toList(),
                comment = state.comment.ifBlank { null }
            ).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.emit(RatingEvent.RatingSubmitted)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isSubmitting = false, error = throwable.message) }
                }
            )
        }
    }
}
