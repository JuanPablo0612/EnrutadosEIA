package com.juanpablo0612.carpool.domain.booking.repository

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Result<Unit>
    fun getPassengerBookings(passengerId: String): Flow<List<Booking>>
    fun getDriverBookingRequests(driverId: String): Flow<List<Booking>>
    fun getBookingsForVehicleOnRoute(routeId: String, vehicleId: String): Flow<List<Booking>>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun hasActiveBooking(passengerId: String, routeId: String, vehicleId: String): Result<Boolean>
}
