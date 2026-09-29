package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.presentation.mytrips.MyTripsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val myTripsModule = module {
    viewModel { MyTripsViewModel(get(), get(), get(), get()) }
}
