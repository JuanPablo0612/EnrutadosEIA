package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(get(), get(), get(), get(), get(), get(), get())
    }
}
