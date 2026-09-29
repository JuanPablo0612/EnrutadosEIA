package com.juanpablo0612.carpool.presentation.booking.driver.decision

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.presentation.booking.BookingError

data class BookingDecisionsState(
    /** Bookings whose accept, reject or cancel is in flight: their buttons wait. */
    val busyIds: Set<String> = emptySet(),
    val rejection: RejectionDraft? = null,
    /** The seat whose cancellation is waiting for the driver to confirm. */
    val cancelling: Booking? = null,
    val error: BookingError? = null,
)

/** The reject sheet's answers so far. */
data class RejectionDraft(
    val bookingId: String,
    val reason: RejectReason? = null,
    val comment: String = "",
)
