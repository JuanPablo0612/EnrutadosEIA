package com.juanpablo0612.carpool.data.booking.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.booking.datasource.BookingRemoteDataSource
import com.juanpablo0612.carpool.data.booking.model.BookingDto
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookingRepositoryImpl(
    private val remoteDataSource: BookingRemoteDataSource
) : BookingRepository {

    override suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            val dto = BookingDto.fromDomain(booking)
            remoteDataSource.createBooking(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.BookingException.Unknown)
        }
    }

    override fun getPassengerBookings(passengerId: String): Flow<List<Booking>> {
        return remoteDataSource.getPassengerBookings(passengerId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getDriverBookingRequests(driverId: String): Flow<List<Booking>> {
        return remoteDataSource.getDriverBookingRequests(driverId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getAllDriverBookings(driverId: String): Flow<List<Booking>> {
        return remoteDataSource.getAllDriverBookings(driverId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<Booking>> {
        return remoteDataSource.getBookingsForTripAsDriver(tripId, driverId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<Booking>> {
        return remoteDataSource.getBookingsForTripAsPassenger(tripId, passengerId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        val statusString = when (status) {
            is BookingStatus.Pending -> "PENDING"
            is BookingStatus.Confirmed -> "CONFIRMED"
            is BookingStatus.Rejected -> "REJECTED"
            is BookingStatus.Cancelled -> "CANCELLED"
        }
        return applyStatusTransition(bookingId, statusString)
    }

    override suspend fun rejectBookingWithReason(
        bookingId: String,
        reason: RejectReason,
        comment: String?,
    ): Result<Unit> {
        val reasonString = when (reason) {
            RejectReason.TripFull -> "TRIP_FULL"
            RejectReason.TripCancelled -> "TRIP_CANCELLED"
            RejectReason.PickupNotPossible -> "PICKUP_NOT_POSSIBLE"
            RejectReason.Other -> "OTHER"
        }
        val extraFields = buildList<Pair<String, Any?>> {
            add("rejectReason" to reasonString)
            if (comment != null) add("rejectComment" to comment)
        }
        return applyStatusTransition(bookingId, "REJECTED", extraFields)
    }

    private suspend fun applyStatusTransition(
        bookingId: String,
        newStatus: String,
        extraFields: List<Pair<String, Any?>> = emptyList(),
    ): Result<Unit> {
        return try {
            remoteDataSource.applyStatusTransition(bookingId, newStatus, extraFields)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.BookingException.Unknown)
        }
    }

    override suspend fun hasActiveBooking(passengerId: String, tripId: String): Result<Boolean> {
        return try {
            val hasActive = remoteDataSource.hasActiveBooking(passengerId, tripId)
            Result.success(hasActive)
        } catch (_: Exception) {
            Result.failure(AppException.BookingException.Unknown)
        }
    }
}
