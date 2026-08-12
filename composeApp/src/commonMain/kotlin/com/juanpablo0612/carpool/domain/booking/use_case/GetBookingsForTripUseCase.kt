package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

// Routes to whichever party-scoped repository query matches the caller, so the resulting
// Firestore query always carries `driverId == uid` or `passengerId == uid` and stays statically
// provable against firestore.rules. TripTrackingViewModel is the only caller: the driver gets the
// full CONFIRMED passenger list, the passenger gets only their own booking.
class GetBookingsForTripUseCase(private val repository: BookingRepository) {
    operator fun invoke(tripId: String, userId: String, isDriver: Boolean): Flow<List<Booking>> =
        if (isDriver) {
            repository.getBookingsForTripAsDriver(tripId, userId)
        } else {
            repository.getBookingsForTripAsPassenger(tripId, userId)
        }
}
