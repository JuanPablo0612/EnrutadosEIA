package com.juanpablo0612.carpool.presentation.auth

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

// CATEGORY_APP_EMAIL opens whatever the user's default (or only) email app is, without needing to
// know its package name or a mailto: target. FLAG_ACTIVITY_NEW_TASK is needed because the Context
// injected via Koin's androidContext() is the Application context, not an Activity.
private class AndroidEmailAppLauncher(private val context: Context) : EmailAppLauncher {
    override fun openEmailApp(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}

actual fun createEmailAppLauncher(context: Any?): EmailAppLauncher =
    AndroidEmailAppLauncher(context as Context)
