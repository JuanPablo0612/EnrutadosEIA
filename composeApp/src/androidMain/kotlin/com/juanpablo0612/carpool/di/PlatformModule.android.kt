package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.preferences.createDataStore
import com.juanpablo0612.carpool.presentation.trip.tracking.EmergencyDialer
import com.juanpablo0612.carpool.presentation.trip.tracking.LocationSharer
import com.juanpablo0612.carpool.presentation.trip.tracking.createEmergencyDialer
import com.juanpablo0612.carpool.presentation.trip.tracking.createLocationSharer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }
    single<EmergencyDialer> { createEmergencyDialer(androidContext()) }
    single<LocationSharer> { createLocationSharer(androidContext()) }
}
