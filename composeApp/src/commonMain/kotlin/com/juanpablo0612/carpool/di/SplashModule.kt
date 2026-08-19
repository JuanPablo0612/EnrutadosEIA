package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashViewModel(get(), get()) }
}
