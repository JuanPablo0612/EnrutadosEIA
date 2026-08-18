package com.juanpablo0612.carpool.data.rating.datasource

import com.juanpablo0612.carpool.data.rating.model.RatingDto
import dev.gitlive.firebase.firestore.Direction
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

    override suspend fun getRatingsForUser(userId: String): List<RatingDto> {
        val snapshot = firestore.collection(COLLECTION)
            .where { "rateeId" equalTo userId }
            .orderBy("createdAt", Direction.DESCENDING)
            .limit(50)
            .get()
        return snapshot.documents.map { it.data(RatingDto.serializer()) }
    }

    companion object {
        private const val COLLECTION = "ratings"
    }
}
