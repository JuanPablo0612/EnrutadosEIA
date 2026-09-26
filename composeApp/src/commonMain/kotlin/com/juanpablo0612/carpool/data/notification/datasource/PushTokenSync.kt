package com.juanpablo0612.carpool.data.notification.datasource

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Keeps this device's push token registered for whoever is signed in, for the whole app process.
 * Started once from the Application; sign-out unregisters in FirebaseAuthRemoteDataSource.
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
                .distinctUntilChanged()
                .filterNotNull()
                .collect { uid -> runCatching { pushTokens.register(uid) } }
        }
    }

    /** Re-registers after FCM rotates this device's token. */
    fun onNewToken() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        scope.launch { runCatching { pushTokens.register(uid) } }
    }
}
