package com.juanpablo0612.carpool.presentation.trip.tracking

import android.content.Context
import android.content.Intent
import android.net.Uri

// ACTION_SENDTO with an "smsto:" URI opens the default messaging app pre-addressed and pre-filled
// but does not send anything itself, so — like ACTION_DIAL — it needs no runtime permission.
// FLAG_ACTIVITY_NEW_TASK is needed because the injected Context is the Application context.
private class AndroidLocationSharer(private val context: Context) : LocationSharer {
    override fun share(phoneNumbers: List<String>, message: String) {
        val recipients = phoneNumbers.joinToString(";")
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$recipients")).apply {
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

actual fun createLocationSharer(context: Any?): LocationSharer =
    AndroidLocationSharer(context as Context)
