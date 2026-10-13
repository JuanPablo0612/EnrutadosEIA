package com.juanpablo0612.carpool.data.notification.datasource

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

/**
 * Keeps this device's push token registered for whoever is signed in, for the whole app process.
 * Started once from the Application; sign-out also unregisters in FirebaseAuthRemoteDataSource.
 */
class PushTokenSync(
    private val firebaseAuth: FirebaseAuth,
    private val pushTokens: PushTokenRemoteDataSource,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        scope.launch {
            firebaseAuth.authStateChanged
                .map { it?.uid }
                .pushTokenActions()
                .collect { action ->
                    // Bounded: a Firestore write waits for the server's ack, and offline that wait
                    // can outlive the user, which would hold back every later action. The write
                    // itself is already queued locally by then.
                    withTimeoutOrNull(ACTION_TIMEOUT) {
                        runCatching {
                            when (action) {
                                is PushTokenAction.Register -> pushTokens.register(action.userId)
                                PushTokenAction.Invalidate -> pushTokens.deleteLocalToken()
                            }
                        }
                    }
                }
        }
    }

    /** Re-registers after FCM rotates this device's token. */
    fun onNewToken() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        scope.launch { runCatching { pushTokens.register(uid) } }
    }

    private companion object {
        val ACTION_TIMEOUT = 10.seconds
    }
}

internal sealed interface PushTokenAction {
    data class Register(val userId: String) : PushTokenAction

    /** Stop this device receiving the previous user's pushes. */
    data object Invalidate : PushTokenAction
}

/**
 * Turns the signed-in user id over time into token work: register for each user, and invalidate
 * whenever a user signs out. That includes sign-outs Firebase forces (password changed elsewhere,
 * account disabled), which skip the logout flow that would otherwise unregister the token; once
 * invalidated, FCM reports the token dead and the backend prunes it.
 */
internal fun Flow<String?>.pushTokenActions(): Flow<PushTokenAction> = flow {
    var previous: String? = null
    distinctUntilChanged().collect { userId ->
        when {
            userId != null -> emit(PushTokenAction.Register(userId))
            previous != null -> emit(PushTokenAction.Invalidate)
        }
        previous = userId
    }
}
