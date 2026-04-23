package com.juanpablo0612.carpool.data.auth.remote

import com.juanpablo0612.carpool.data.auth.model.UserDto
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseAuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRemoteDataSource {

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean
    ) {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val user = checkNotNull(authResult.user) { "Firebase returned null user after successful sign-up" }
        val userDto = UserDto(
            id = user.uid,
            email = email,
            name = name,
            isEmailVerified = user.isEmailVerified,
            isPassenger = isPassenger,
            isDriver = isDriver
        )
        firestore.collection("users").document(user.uid).set(UserDto.serializer(), userDto)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getCurrentUser(): UserDto {
        val userId = checkNotNull(firebaseAuth.currentUser?.uid) { "User not authenticated" }
        val snapshot = firestore.collection("users").document(userId).get()
        return snapshot.data(UserDto.serializer())
    }
}
