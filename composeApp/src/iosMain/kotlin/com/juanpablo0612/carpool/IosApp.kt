package com.juanpablo0612.carpool

import com.juanpablo0612.carpool.core.config.BuildKonfig
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenSync
import com.juanpablo0612.carpool.di.initKoin
import com.juanpablo0612.carpool.presentation.place.add.components.NativeMapViewFactory
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

/**
 * What the Swift app delegate (iosApp/iosApp/AppDelegate.swift) calls at launch: the iOS
 * counterpart of CarpoolApplication. Firebase must already be configured.
 */
object IosApp {

    /** The Google Maps key from secrets.properties, shared with Android (BuildKonfig is internal). */
    val mapsApiKey: String get() = BuildKonfig.MAPS_API_KEY

    /** Starts Koin and push token sync. [mapViewFactory] provides the Swift-built Google Maps views. */
    fun start(mapViewFactory: NativeMapViewFactory) {
        initKoin {
            modules(module { single { mapViewFactory } })
        }
        KoinPlatform.getKoin().get<PushTokenSync>().start()
    }
}
