package com.juanpablo0612.carpool.core.location

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSBundle
import platform.Foundation.NSThread
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync
import kotlin.coroutines.resume

/**
 * The app's only iOS location permission controller, shared by the place screens'
 * LocationPermissionRequester and the Geolocator so both see the same authorization.
 *
 * It replaces compass 1.2.2's iOS controller, which asks for Always authorization (iOS refuses that
 * without NSLocationAlwaysAndWhenInUseUsageDescription, so no prompt appears and the request never
 * returns), resumes a finished request again on the next authorization change (which terminates
 * the app) and throws on the Restricted status.
 */
internal val iosLocationPermissionController: LocationPermissionController by lazy {
    IosLocationPermissionController()
}

private class IosLocationPermissionController : LocationPermissionController {

    private val delegate = AuthorizationDelegate()

    // Core Location delivers delegate callbacks on the run loop of the thread that created the
    // manager, which must be the main thread.
    private val manager: CLLocationManager = onMainThread {
        CLLocationManager().also { it.delegate = delegate }
    }

    override fun hasPermission(): Boolean =
        manager.authorizationStatus.toPermissionState() == PermissionState.Granted

    override suspend fun requirePermissionFor(priority: Priority): PermissionState =
        withContext(Dispatchers.Main) {
            val status = manager.authorizationStatus
            when {
                status != kCLAuthorizationStatusNotDetermined -> status.toPermissionState()
                // iOS ignores the request without the purpose string, so waiting would never end.
                !hasWhenInUsePurposeString() -> PermissionState.DeniedForever
                else -> suspendCancellableCoroutine { continuation ->
                    delegate.waiting += continuation
                    manager.requestWhenInUseAuthorization()
                }
            }
        }

    private fun hasWhenInUsePurposeString(): Boolean =
        NSBundle.mainBundle.objectForInfoDictionaryKey("NSLocationWhenInUseUsageDescription") != null
}

/** Only touched on the main thread: requests run there and so do Core Location's callbacks. */
private class AuthorizationDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    val waiting = mutableListOf<CancellableContinuation<PermissionState>>()

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        // Also called when the manager is created and while the prompt is up; only an answer
        // ends the wait. Each request is resumed once, then forgotten.
        if (status == kCLAuthorizationStatusNotDetermined || waiting.isEmpty()) return
        val answered = waiting.toList()
        waiting.clear()
        // A cancelled caller stays in the list until here; resuming it is a no-op.
        answered.forEach { if (it.isActive) it.resume(status.toPermissionState()) }
    }
}

/** Denied and Restricted (parental controls, device management) can only change in Settings. */
private fun CLAuthorizationStatus.toPermissionState(): PermissionState = when (this) {
    kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse -> PermissionState.Granted
    kCLAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
    else -> PermissionState.DeniedForever
}

private fun <T> onMainThread(block: () -> T): T {
    if (NSThread.isMainThread) return block()
    var result: Result<T>? = null
    dispatch_sync(dispatch_get_main_queue()) { result = runCatching(block) }
    return result!!.getOrThrow()
}
