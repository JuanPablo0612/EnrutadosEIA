package com.juanpablo0612.carpool.data.rating

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.rating.model.Rating
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore

class RatingRepositoryImpl(private val firestore: FirebaseFirestore) : RatingRepository {

    override suspend fun createRating(rating: Rating): Result<Unit> {
        return try {
            val dto = RatingDto.fromDomain(rating)
            firestore.collection(COLLECTION).document(rating.id).set(RatingDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    override suspend fun hasRatedBooking(bookingId: String, raterId: String): Result<Boolean> {
        return try {
            val id = "${bookingId}_$raterId"
            val doc = firestore.collection(COLLECTION).document(id).get()
            Result.success(doc.exists)
        } catch (e: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    override suspend fun getUserAverageRating(userId: String): Result<Double?> {
        return try {
            val snapshot = firestore.collection(COLLECTION)
                .where { "rateeId" equalTo userId }
                .orderBy("createdAt", Direction.DESCENDING)
                .limit(50)
                .get()
            val ratings = snapshot.documents.map { it.data(RatingDto.serializer()) }
            val avg = if (ratings.isEmpty()) null else ratings.map { it.stars }.average()
            Result.success(avg)
        } catch (e: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    override suspend fun getUserTotalTrips(userId: String): Result<Int> {
        return try {
            val snapshot = firestore.collection(COLLECTION)
                .where { "rateeId" equalTo userId }
                .get()
            Result.success(snapshot.documents.size)
        } catch (e: Exception) {
            Result.failure(AppException.RatingException.Unknown)
        }
    }

    companion object {
        private const val COLLECTION = "ratings"
    }
}
