package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.presentation.place.add.components.createLocationPermissionRequester
import com.juanpablo0612.carpool.presentation.session.UserSession
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// App-wide presentation-layer state and services shared across features, not owned by any one
// feature's data layer.
val appStateModule = module {
    singleOf(::UserSession)
    single { createLocationPermissionRequester() }
}
