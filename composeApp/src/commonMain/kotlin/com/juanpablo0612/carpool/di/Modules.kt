package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.remote.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.use_case.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.ObserveAuthStateUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
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
