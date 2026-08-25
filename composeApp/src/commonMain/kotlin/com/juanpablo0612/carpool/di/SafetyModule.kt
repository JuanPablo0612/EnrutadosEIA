package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.safety.datasource.FirebaseSafetyRemoteDataSource
import com.juanpablo0612.carpool.data.safety.datasource.SafetyRemoteDataSource
import com.juanpablo0612.carpool.data.safety.repository.SafetyRepositoryImpl
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository
import com.juanpablo0612.carpool.domain.safety.usecase.AddEmergencyContactUseCase
import com.juanpablo0612.carpool.presentation.safety.SafetyViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val safetyModule = module {
    singleOf(::FirebaseSafetyRemoteDataSource) bind SafetyRemoteDataSource::class
    singleOf(::SafetyRepositoryImpl) bind SafetyRepository::class
    factoryOf(::AddEmergencyContactUseCase)
    viewModel { SafetyViewModel(get(), get(), get()) }
}
