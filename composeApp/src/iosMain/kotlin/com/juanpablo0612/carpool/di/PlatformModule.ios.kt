package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.places.service.IosLocationService
import com.juanpablo0612.carpool.data.places.service.IosPlacesSearchService
import com.juanpablo0612.carpool.data.preferences.createDataStore
import com.juanpablo0612.carpool.domain.places.service.LocationService
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore() }
    single { IosLocationService() } bind LocationService::class
    single { IosPlacesSearchService() } bind PlacesSearchService::class
}
