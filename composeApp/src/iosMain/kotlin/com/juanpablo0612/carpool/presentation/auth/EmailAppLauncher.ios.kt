package com.juanpablo0612.carpool.presentation.auth

// No-op on iOS: the iOS target is an inactive scaffold (see CLAUDE.md), so this seam is left as a placeholder rather than wiring up a real
// UIApplication.openURL(message://) call that could not be built/verified without an iOS
// toolchain here.
private class IosEmailAppLauncher : EmailAppLauncher {
    override fun openEmailApp(): Boolean {
        // no-op — iOS scaffold is inactive
        return false
    }
}

actual fun createEmailAppLauncher(context: Any?): EmailAppLauncher = IosEmailAppLauncher()
