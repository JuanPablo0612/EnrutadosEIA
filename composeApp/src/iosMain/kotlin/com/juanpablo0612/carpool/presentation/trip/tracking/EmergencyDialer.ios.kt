package com.juanpablo0612.carpool.presentation.trip.tracking

// No-op on iOS, matching the MapPreview.ios.kt precedent: the iOS target is an inactive scaffold
// (see CLAUDE.md), so this seam is left as a placeholder rather than wiring up a real
// UIApplication.openURL(tel:) call that could not be built/verified without an iOS toolchain here.
private class IosEmergencyDialer : EmergencyDialer {
    override fun dial(phoneNumber: String) {
        // no-op — iOS scaffold is inactive
    }
}

actual fun createEmergencyDialer(context: Any?): EmergencyDialer = IosEmergencyDialer()
