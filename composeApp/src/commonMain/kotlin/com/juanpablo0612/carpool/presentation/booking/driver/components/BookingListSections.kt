package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.juanpablo0612.carpool.presentation.booking.model.BookingWithPassenger

/** Shared by BookingRequestsScreen's Pending tab and TripPassengersScreen's pending section —
 *  both render the same [BookingRequestCard] wiring against a driver's own booking list. */
fun LazyListScope.pendingBookingItems(
    items: List<BookingWithPassenger>,
    processingIds: Set<String>,
    nowMs: Long,
    key: (BookingWithPassenger) -> Any,
    onAccept: (bookingId: String, tripId: String) -> Unit,
    onReject: (bookingId: String) -> Unit,
    onViewProfile: (passengerId: String) -> Unit,
) {
    items(items, key = key) { item ->
        BookingRequestCard(
            item = item,
            processingIds = processingIds,
            nowMs = nowMs,
            onAccept = onAccept,
            onReject = onReject,
            onViewProfile = onViewProfile,
        )
    }
}

/** Shared by BookingRequestsScreen's Confirmed tab and TripPassengersScreen's confirmed section —
 *  both render the same [ConfirmedBookingCard] wiring, including the "is this trip already past"
 *  derivation that gates whether messaging opens a read-only chat. */
fun LazyListScope.confirmedBookingItems(
    items: List<BookingWithPassenger>,
    processingIds: Set<String>,
    nowMs: Long,
    key: (BookingWithPassenger) -> Any,
    onMessage: (item: BookingWithPassenger, isPast: Boolean) -> Unit,
    onCancel: (bookingId: String) -> Unit,
    onRate: (item: BookingWithPassenger) -> Unit,
) {
    items(items, key = key) { item ->
        val isPast = item.booking.departureTime <= nowMs
        ConfirmedBookingCard(
            item = item,
            processingIds = processingIds,
            nowMs = nowMs,
            onMessage = { onMessage(item, isPast) },
            onCancel = { onCancel(item.booking.id) },
            onRate = { onRate(item) },
        )
    }
}
