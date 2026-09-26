package com.juanpablo0612.carpool

import android.app.Application
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenSync
import com.juanpablo0612.carpool.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class CarpoolApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CarpoolApplication)
        }
        GlobalContext.get().get<PushTokenSync>().start()
    }
}
