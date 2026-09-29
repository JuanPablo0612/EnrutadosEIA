package com.juanpablo0612.carpool.presentation.booking.driver.decision

import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The driver's answers to bookings, shared by every screen that lists them ("Solicitudes" and a
 * trip's passengers) so accepting, rejecting and cancelling behave the same everywhere. The owning
 * ViewModel creates it with its own [scope] and forwards [BookingDecisionAction]s to [onAction].
 */
class BookingDecisions(
    private val confirmBooking: ConfirmBookingUseCase,
    private val rejectBooking: RejectBookingUseCase,
    private val cancelBooking: CancelBookingUseCase,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(BookingDecisionsState())
    val state: StateFlow<BookingDecisionsState> = _state.asStateFlow()

    fun onAction(action: BookingDecisionAction) {
        when (action) {
            is BookingDecisionAction.Accept -> run(action.booking.id) {
                confirmBooking(action.booking.id, action.booking.tripId)
            }
            is BookingDecisionAction.Reject -> _state.update {
                it.copy(rejection = RejectionDraft(bookingId = action.booking.id, reason = action.suggestedReason))
            }
            is BookingDecisionAction.OnRejectReasonSelected -> _state.update {
                it.copy(rejection = it.rejection?.copy(reason = action.reason))
            }
            is BookingDecisionAction.OnRejectCommentChanged -> _state.update {
                it.copy(rejection = it.rejection?.copy(comment = action.comment))
            }
            BookingDecisionAction.ConfirmReject -> {
                val draft = _state.value.rejection ?: return
                val reason = draft.reason ?: return
                _state.update { it.copy(rejection = null) }
                run(draft.bookingId) {
                    rejectBooking(draft.bookingId, reason, draft.comment.trim().ifEmpty { null })
                }
            }
            BookingDecisionAction.DismissReject -> _state.update { it.copy(rejection = null) }
            is BookingDecisionAction.Cancel -> _state.update { it.copy(cancelling = action.booking) }
            BookingDecisionAction.ConfirmCancel -> {
                val bookingId = _state.value.cancelling?.id ?: return
                _state.update { it.copy(cancelling = null) }
                run(bookingId) { cancelBooking(bookingId) }
            }
            BookingDecisionAction.DismissCancel -> _state.update { it.copy(cancelling = null) }
            BookingDecisionAction.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    /** Runs [block] for [bookingId] unless one is already running for it (a double tap). */
    private fun run(bookingId: String, block: suspend () -> Result<Unit>) {
        if (bookingId in _state.value.busyIds) return
        _state.update { it.copy(busyIds = it.busyIds + bookingId, error = null) }
        scope.launch {
            val error = block().exceptionOrNull()?.toBookingError()
            _state.update { it.copy(busyIds = it.busyIds - bookingId, error = error ?: it.error) }
        }
    }
}
