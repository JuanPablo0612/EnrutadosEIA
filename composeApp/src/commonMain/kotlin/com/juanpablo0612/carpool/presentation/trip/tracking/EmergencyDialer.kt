package com.juanpablo0612.carpool.presentation.trip.tracking

/**
 * Places a phone call using the platform's dialer app. Mirrors the
 * [com.juanpablo0612.carpool.presentation.place.add.components.LocationPermissionRequester]
 * expect/actual seam: the interface + factory live in commonMain, the platform implementations
 * live in their respective source sets.
 */
interface EmergencyDialer {
    /**
     * Opens the platform dialer pre-filled with [phoneNumber]. Uses ACTION_DIAL semantics (not
     * ACTION_CALL) on Android, so the user must still tap "call" themselves — this needs no
     * CALL_PHONE runtime permission.
     */
    fun dial(phoneNumber: String)
}

/**
 * Creates the platform [EmergencyDialer]. [context] is the Android `Context` — obtained from
 * Koin's `androidContext()` inside `di/PlatformModule.android.kt`, the same seam already used by
 * `data/preferences/DataStoreFactory.kt`'s `createDataStore(context: Any? = null)` — and is
 * ignored on iOS.
 */
expect fun createEmergencyDialer(context: Any? = null): EmergencyDialer
