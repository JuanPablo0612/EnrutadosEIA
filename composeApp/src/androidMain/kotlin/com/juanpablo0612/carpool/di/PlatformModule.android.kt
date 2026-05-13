package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.places.service.AndroidLocationService
import com.juanpablo0612.carpool.data.places.service.AndroidPlacesSearchService
import com.juanpablo0612.carpool.data.preferences.createDataStore
import com.juanpablo0612.carpool.domain.places.service.LocationService
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }
    single { AndroidLocationService(androidContext()) } bind LocationService::class
    single { AndroidPlacesSearchService(androidContext()) } bind PlacesSearchService::class
}
