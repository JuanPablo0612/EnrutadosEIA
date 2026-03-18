package com.juanpablo0612.carpool

import android.app.Application
import com.juanpablo0612.carpool.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CarpoolApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CarpoolApplication)
        }
    }
}