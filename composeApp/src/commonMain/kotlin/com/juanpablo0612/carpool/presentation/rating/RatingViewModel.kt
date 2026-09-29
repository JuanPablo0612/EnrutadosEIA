package com.juanpablo0612.carpool.presentation.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import com.juanpablo0612.carpool.domain.rating.usecase.CreateRatingUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RatingViewModel(
    private val target: RatingTarget,
    private val createRatingUseCase: CreateRatingUseCase,
    private val authRepository: AuthRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RatingUiState(target = target))
    val state: StateFlow<RatingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RatingEvent>()
    val events: SharedFlow<RatingEvent> = _events.asSharedFlow()

    init {
        checkAlreadyRated()
    }

    private fun checkAlreadyRated() {
        val raterId = authRepository.getCurrentUserId()
        if (raterId == null) {
            _state.update { it.copy(isLoading = false, alreadyRated = false) }
            return
        }
        viewModelScope.launch {
            ratingRepository.hasRatedBooking(target.bookingId, raterId).fold(
                onSuccess = { hasRated ->
                    _state.update { it.copy(isLoading = false, alreadyRated = hasRated) }
                },
                onFailure = {
                    // Fail open: don't block the user from rating just because the pre-check
                    // itself failed. The post-submit AlreadyRated handling remains as a backstop.
                    _state.update { it.copy(isLoading = false, alreadyRated = false) }
                }
            )
        }
    }

    fun onAction(action: RatingAction) {
        when (action) {
            is RatingAction.OnStarSelect -> _state.update { it.copy(selectedStars = action.stars) }
            is RatingAction.OnChipToggle -> {
                val chips = _state.value.selectedChips.toMutableSet()
                if (action.chip in chips) chips.remove(action.chip) else chips.add(action.chip)
                _state.update { it.copy(selectedChips = chips) }
            }
            is RatingAction.OnCommentChange ->
                _state.update { it.copy(comment = action.comment.take(MAX_COMMENT_LENGTH)) }
            RatingAction.OnSubmit -> submit()
            RatingAction.OnSkip -> viewModelScope.launch { _events.emit(RatingEvent.Skipped) }
        }
    }

    private fun submit() {
        val state = _state.value
        if (!state.canSubmit) return
        val raterId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            createRatingUseCase(
                tripId = target.tripId,
                bookingId = target.bookingId,
                raterId = raterId,
                rateeId = target.rateeId,
                stars = state.selectedStars,
                chips = state.selectedChips.toList(),
                comment = state.comment.trim().ifEmpty { null }
            ).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.emit(RatingEvent.RatingSubmitted)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isSubmitting = false, error = throwable.toRatingError()) }
                }
            )
        }
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 200
    }
}
