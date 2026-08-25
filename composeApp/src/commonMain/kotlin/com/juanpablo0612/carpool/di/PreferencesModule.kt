package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.preferences.datasource.UserPreferencesLocalDataSource
import com.juanpablo0612.carpool.data.preferences.repository.UserPreferencesRepositoryImpl
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val preferencesModule = module {
    // DataStore singleton is provided by platformModule (platform-specific)
    singleOf(::UserPreferencesLocalDataSource)
    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
    viewModel { OnboardingViewModel(get()) }
}
