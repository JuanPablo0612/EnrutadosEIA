package com.juanpablo0612.carpool.data.rating.datasource

import com.juanpablo0612.carpool.data.rating.model.RatingDto
import com.juanpablo0612.carpool.data.rating.model.UserRatingSummaryDto
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseRatingRemoteDataSource(
    private val firestore: FirebaseFirestore
) : RatingRemoteDataSource {

    override suspend fun createRating(rating: RatingDto) {
        firestore.collection(COLLECTION).document(rating.id).set(RatingDto.serializer(), rating)
    }

    override suspend fun hasRatedBooking(bookingId: String, raterId: String): Boolean {
        val id = "${bookingId}_$raterId"
        val doc = firestore.collection(COLLECTION).document(id).get()
        return doc.exists
    }

    override suspend fun getUserRatingSummary(userId: String): UserRatingSummaryDto {
        val doc = firestore.collection(USERS_COLLECTION).document(userId).get()
        return if (doc.exists) doc.data(UserRatingSummaryDto.serializer()) else UserRatingSummaryDto()
    }

    companion object {
        private const val COLLECTION = "ratings"
        private const val USERS_COLLECTION = "users"
    }
}
