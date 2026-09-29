package com.juanpablo0612.carpool.presentation.booking.driver.decision

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.RejectReason

/** What a driver can do with a booking on one of their trips, wherever it is listed. */
sealed class BookingDecisionAction {
    data class Accept(val booking: Booking) : BookingDecisionAction()

    /** Opens the reject sheet, with [suggestedReason] preselected when the context implies one. */
    data class Reject(val booking: Booking, val suggestedReason: RejectReason? = null) : BookingDecisionAction()
    data class OnRejectReasonSelected(val reason: RejectReason) : BookingDecisionAction()
    data class OnRejectCommentChanged(val comment: String) : BookingDecisionAction()
    data object ConfirmReject : BookingDecisionAction()
    data object DismissReject : BookingDecisionAction()

    /** Asks before taking back a seat already given. */
    data class Cancel(val booking: Booking) : BookingDecisionAction()
    data object ConfirmCancel : BookingDecisionAction()
    data object DismissCancel : BookingDecisionAction()

    data object DismissError : BookingDecisionAction()
}
