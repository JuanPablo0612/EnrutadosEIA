package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.datasource.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.datasource.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.presentation.auth.emailverification.EmailVerificationViewModel
import com.juanpablo0612.carpool.presentation.auth.forgotpassword.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleOf(::FirebaseAuthRemoteDataSource) bind AuthRemoteDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { EmailVerificationViewModel(get()) }
}
