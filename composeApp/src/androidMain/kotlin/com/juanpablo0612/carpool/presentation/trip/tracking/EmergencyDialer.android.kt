package com.juanpablo0612.carpool.presentation.trip.tracking

import android.content.Context
import android.content.Intent
import android.net.Uri

// ACTION_DIAL (not ACTION_CALL) opens the system dialer pre-filled with the number but leaves the
// final "call" tap to the user, so no CALL_PHONE runtime permission is required. FLAG_ACTIVITY_NEW_TASK
// is needed because the Context injected via Koin's androidContext() is the Application context,
// not an Activity.
private class AndroidEmergencyDialer(private val context: Context) : EmergencyDialer {
    override fun dial(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

actual fun createEmergencyDialer(context: Any?): EmergencyDialer =
    AndroidEmergencyDialer(context as Context)
