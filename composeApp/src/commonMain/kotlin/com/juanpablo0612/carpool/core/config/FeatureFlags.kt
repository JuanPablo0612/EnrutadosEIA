package com.juanpablo0612.carpool.core.config

object FeatureFlags {
    /**
     * Email verification is temporarily disabled so the first round of testing has a
     * frictionless sign-up. The whole verification flow (route, screen, ViewModel, use case,
     * strings) is still wired and functional — set this back to `true` to restore the gate.
     */
    const val EMAIL_VERIFICATION_REQUIRED = false
}
