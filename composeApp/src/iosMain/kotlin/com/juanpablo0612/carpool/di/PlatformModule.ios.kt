package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.preferences.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore() }
}
