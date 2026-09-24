package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.preferences.datasource.createDataStore
import com.juanpablo0612.carpool.presentation.auth.EmailAppLauncher
import com.juanpablo0612.carpool.presentation.auth.createEmailAppLauncher
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }
    single<EmailAppLauncher> { createEmailAppLauncher(androidContext()) }
}
