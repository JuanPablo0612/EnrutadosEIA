package com.juanpablo0612.carpool.data.auth.datasource

import com.juanpablo0612.carpool.core.config.FeatureFlags
import com.juanpablo0612.carpool.data.auth.model.UserDto
import com.juanpablo0612.carpool.data.vehicle.datasource.upload
import dev.gitlive.firebase.auth.FirebaseAuth
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenRemoteDataSource
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class FirebaseAuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions,
    private val pushTokens: PushTokenRemoteDataSource,
) : AuthRemoteDataSource {

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
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
        // Stop this device receiving the user's pushes. Best effort and bounded: signing out
        // must work offline, and the backend prunes tokens FCM later reports as dead.
        firebaseAuth.currentUser?.uid?.let { uid ->
            withTimeoutOrNull(SIGN_OUT_TOKEN_TIMEOUT) { runCatching { pushTokens.unregister(uid) } }
        }
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

    override suspend fun updateProfile(name: String, phone: String?, bio: String?, photoBytes: ByteArray?): UserDto {
        val userId = checkNotNull(firebaseAuth.currentUser?.uid) { "User not authenticated" }
        val updates = mutableMapOf<String, Any?>(
            "name" to name,
            "phone" to phone,
            "bio" to bio
        )
        if (photoBytes != null) {
            // Mirrors signUp()'s compress-then-upload-to-users/{uid}/profile.jpg flow.
            val compressed = FileKit.compressImage(
                bytes = photoBytes,
                quality = 80,
                imageFormat = ImageFormat.JPEG
            )
            val ref = storage.reference.child("users/$userId/profile.jpg")
            ref.upload(compressed)
            updates["photoUrl"] = ref.getDownloadUrl()
        }
        firestore.collection("users").document(userId).update(updates)
        val snapshot = firestore.collection("users").document(userId).get()
        return snapshot.data(UserDto.serializer())
    }

    override suspend fun deleteAccount() {
        checkNotNull(firebaseAuth.currentUser) { "No authenticated user" }
        // The deleteAccount function purges the user's data and then the auth user, without
        // requiring a recent sign-in the way the client SDK's user.delete() does.
        functions.httpsCallable(DELETE_ACCOUNT_FUNCTION, DELETE_ACCOUNT_TIMEOUT).invoke()
        runCatching { pushTokens.deleteLocalToken() }
        firebaseAuth.signOut()
    }

    private companion object {
        const val DELETE_ACCOUNT_FUNCTION = "deleteAccount"
        val DELETE_ACCOUNT_TIMEOUT = 120.seconds
        val SIGN_OUT_TOKEN_TIMEOUT = 3.seconds
    }
}
