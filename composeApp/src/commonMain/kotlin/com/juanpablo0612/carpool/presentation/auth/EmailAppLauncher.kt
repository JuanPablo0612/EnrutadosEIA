package com.juanpablo0612.carpool.presentation.auth

/**
 * Opens the platform's email app. Mirrors the
 * [com.juanpablo0612.carpool.presentation.trip.tracking.EmergencyDialer] expect/actual seam: the
 * interface + factory live in commonMain, the platform implementations live in their respective
 * source sets.
 */
interface EmailAppLauncher {
    /** @return `true` if an email app handled the request, `false` if none on the device could. */
    fun openEmailApp(): Boolean
}

/**
 * Creates the platform [EmailAppLauncher]. [context] is the Android `Context` — obtained from
 * Koin's `androidContext()` inside `di/PlatformModule.android.kt` — and is ignored on iOS.
 */
expect fun createEmailAppLauncher(context: Any? = null): EmailAppLauncher
