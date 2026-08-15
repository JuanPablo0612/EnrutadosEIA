package com.juanpablo0612.carpool.presentation.trip.tracking

// No-op on iOS — same rationale as EmergencyDialer.ios.kt: the iOS target is an inactive scaffold.
private class IosLocationSharer : LocationSharer {
    override fun share(phoneNumbers: List<String>, message: String) {
        // no-op — iOS scaffold is inactive
    }
}

actual fun createLocationSharer(context: Any?): LocationSharer = IosLocationSharer()
