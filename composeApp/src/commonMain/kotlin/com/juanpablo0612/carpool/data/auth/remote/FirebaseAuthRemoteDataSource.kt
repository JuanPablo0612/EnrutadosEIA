package com.juanpablo0612.carpool.data.auth.remote

import com.juanpablo0612.carpool.data.auth.model.UserDto
import com.juanpablo0612.carpool.data.vehicles.upload
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage

class FirebaseAuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRemoteDataSource {

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean,
        phone: String,
        photoBytes: ByteArray?
    ) {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val user = checkNotNull(authResult.user) { "Firebase returned null user after successful sign-up" }

        var photoUrl: String? = null
        if (photoBytes != null) {
            val compressed = FileKit.compressImage(
                bytes = photoBytes,
                quality = 80,
                imageFormat = ImageFormat.JPEG
            )
            val ref = storage.reference.child("users/${user.uid}/profile.jpg")
            ref.upload(compressed)
            photoUrl = ref.getDownloadUrl()
        }

        val userDto = UserDto(
            id = user.uid,
            email = email,
            name = name,
            isEmailVerified = user.isEmailVerified,
            isPassenger = isPassenger,
            isDriver = isDriver,
            phone = phone.ifBlank { null },
            photoUrl = photoUrl
        )
        firestore.collection("users").document(user.uid).set(UserDto.serializer(), userDto)

        user.sendEmailVerification()
    }

    override suspend fun sendEmailVerification() {
        val user = checkNotNull(firebaseAuth.currentUser) { "No authenticated user" }
        user.sendEmailVerification()
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
