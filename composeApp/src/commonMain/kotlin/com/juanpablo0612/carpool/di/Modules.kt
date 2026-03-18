package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.datasource.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.datasource.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.domain.repository.AuthRepository
import com.juanpablo0612.carpool.domain.usecase.LoginUseCase
import com.juanpablo0612.carpool.domain.usecase.LogoutUseCase
import com.juanpablo0612.carpool.domain.usecase.ObserveAuthStateUseCase
import com.juanpablo0612.carpool.domain.usecase.RegisterUseCase
import com.juanpablo0612.carpool.presentation.viewmodel.AuthViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single { Firebase.auth }
    singleOf(::FirebaseAuthRemoteDataSource) bind AuthRemoteDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveAuthStateUseCase)

    viewModelOf(::AuthViewModel)
}

val appModule = module {
    includes(authModule)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
