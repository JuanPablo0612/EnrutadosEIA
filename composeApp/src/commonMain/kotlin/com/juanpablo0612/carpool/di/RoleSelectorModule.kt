package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.presentation.roleselector.RoleSelectorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roleSelectorModule = module {
    viewModel { RoleSelectorViewModel(get(), get(), get()) }
}
