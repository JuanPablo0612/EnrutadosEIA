package com.juanpablo0612.carpool.data.auth.datasource

import com.juanpablo0612.carpool.core.config.FeatureFlags
import com.juanpablo0612.carpool.data.auth.model.UserDto
import com.juanpablo0612.carpool.data.vehicle.datasource.upload
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

        // Email verification is temporarily bypassed (FeatureFlags.EMAIL_VERIFICATION_REQUIRED)
        // for frictionless testing sign-ups, so skip sending the verification email to avoid
        // confusing testers with a stray email they don't need to act on.
        if (FeatureFlags.EMAIL_VERIFICATION_REQUIRED) {
            user.sendEmailVerification()
        }
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
        val user = checkNotNull(firebaseAuth.currentUser) { "User not authenticated" }
        // The Firestore isEmailVerified field is only a copy written once at sign-up; the auth
        // token is the source of truth, so refresh it before trusting isEmailVerified.
        user.reload()
        val isVerified = firebaseAuth.currentUser?.isEmailVerified ?: user.isEmailVerified
        val snapshot = firestore.collection("users").document(user.uid).get()
        val dto = snapshot.data(UserDto.serializer())
        if (isVerified && !dto.isEmailVerified) {
            firestore.collection("users").document(user.uid).update(mapOf("isEmailVerified" to true))
        }
        return dto.copy(isEmailVerified = isVerified)
    }

    override suspend fun getPublicProfile(userId: String): UserDto {
        val snapshot = firestore.collection("users").document(userId).get()
        return snapshot.data(UserDto.serializer())
    }

    override suspend fun updateProfile(name: String, phone: String?, bio: String?, photoUrl: String?): UserDto {
        val userId = checkNotNull(firebaseAuth.currentUser?.uid) { "User not authenticated" }
        val updates = mutableMapOf<String, Any?>(
            "name" to name,
            "phone" to phone,
            "bio" to bio
        )
        if (photoUrl != null) updates["photoUrl"] = photoUrl
        firestore.collection("users").document(userId).update(updates)
        val snapshot = firestore.collection("users").document(userId).get()
        return snapshot.data(UserDto.serializer())
    }

    override suspend fun updateRoles(isDriver: Boolean, isPassenger: Boolean): UserDto {
        val userId = checkNotNull(firebaseAuth.currentUser?.uid) { "User not authenticated" }
        firestore.collection("users").document(userId).update(
            mapOf("isDriver" to isDriver, "isPassenger" to isPassenger)
        )
        val snapshot = firestore.collection("users").document(userId).get()
        return snapshot.data(UserDto.serializer())
    }

    override suspend fun deleteAccount() {
        val user = checkNotNull(firebaseAuth.currentUser) { "No authenticated user" }
        // user.delete() requires a recent sign-in and commonly fails with a stale session. Delete
        // the auth user first so a failure here leaves the profile document intact (recoverable);
        // deleting the document first would leave an authenticated user with no profile, which
        // UserDto's now-defaulted fields decode without throwing but is still a broken state (4.3).
        user.delete()
        firestore.collection("users").document(user.uid).delete()
    }
}
