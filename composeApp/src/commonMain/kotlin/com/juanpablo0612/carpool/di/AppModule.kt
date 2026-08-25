package com.juanpablo0612.carpool.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    includes(
        platformModule,
        firebaseModule,
        appStateModule,
        authModule,
        splashModule,
        profileModule,
        preferencesModule,
        roleSelectorModule,
        routeModule,
        tripModule,
        placeModule,
        vehicleModule,
        bookingModule,
        homeModule,
        ratingModule,
        chatModule,
        notificationModule,
        safetyModule
    )
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
