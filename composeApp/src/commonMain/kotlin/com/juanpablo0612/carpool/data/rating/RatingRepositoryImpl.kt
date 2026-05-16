package com.juanpablo0612.carpool.data.rating

import com.juanpablo0612.carpool.domain.rating.model.Rating
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore

class RatingRepositoryImpl(private val firestore: FirebaseFirestore) : RatingRepository {

    override suspend fun createRating(rating: Rating): Result<Unit> {
        return try {
            val dto = RatingDto.fromDomain(rating)
            firestore.collection(COLLECTION).document(rating.id).set(RatingDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasRatedBooking(bookingId: String, raterId: String): Result<Boolean> {
        return try {
            val id = "${bookingId}_$raterId"
            val doc = firestore.collection(COLLECTION).document(id).get()
            Result.success(doc.exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserAverageRating(userId: String): Result<Double?> {
        return try {
            val snapshot = firestore.collection(COLLECTION).get()
            val ratings = snapshot.documents
                .map { it.data(RatingDto.serializer()) }
                .filter { it.rateeId == userId }
                .takeLast(50)
            val avg = if (ratings.isEmpty()) null else ratings.map { it.stars }.average()
            Result.success(avg)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserTotalTrips(userId: String): Result<Int> {
        return try {
            val snapshot = firestore.collection(COLLECTION).get()
            val count = snapshot.documents
                .map { it.data(RatingDto.serializer()) }
                .count { it.rateeId == userId }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION = "ratings"
    }
}
