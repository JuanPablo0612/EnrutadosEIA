package com.juanpablo0612.carpool.data.booking.repository

import com.juanpablo0612.carpool.data.booking.model.BookingDto
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val dto = BookingDto.fromDomain(booking).copy(id = docRef.id)
            docRef.set(BookingDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPassengerBookings(passengerId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(BookingDto.serializer()).toDomain() }
                    .filter { it.passengerId == passengerId }
            }
    }

    override fun getDriverBookingRequests(driverId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(BookingDto.serializer()).toDomain() }
                    .filter { it.driverId == driverId && it.status is BookingStatus.Pending }
            }
    }

    override fun getBookingsForVehicleOnRoute(routeId: String, vehicleId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(BookingDto.serializer()).toDomain() }
                    .filter { it.routeId == routeId && it.vehicleId == vehicleId }
            }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            val statusString = when (status) {
                is BookingStatus.Pending -> "PENDING"
                is BookingStatus.Confirmed -> "CONFIRMED"
                is BookingStatus.Rejected -> "REJECTED"
                is BookingStatus.Cancelled -> "CANCELLED"
            }
            firestore.collection(COLLECTION_NAME).document(bookingId)
                .update("status" to statusString)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasActiveBooking(
        passengerId: String,
        routeId: String,
        vehicleId: String
    ): Result<Boolean> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get()
            val hasActive = snapshot.documents
                .map { it.data(BookingDto.serializer()).toDomain() }
                .any { booking ->
                    booking.passengerId == passengerId &&
                        booking.routeId == routeId &&
                        booking.vehicleId == vehicleId &&
                        (booking.status is BookingStatus.Pending || booking.status is BookingStatus.Confirmed)
                }
            Result.success(hasActive)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "bookings"
    }
}
