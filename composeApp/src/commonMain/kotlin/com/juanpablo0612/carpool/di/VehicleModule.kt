package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.vehicle.datasource.FirebaseVehicleRemoteDataSource
import com.juanpablo0612.carpool.data.vehicle.datasource.FirebaseVehicleStorageDataSource
import com.juanpablo0612.carpool.data.vehicle.datasource.VehicleRemoteDataSource
import com.juanpablo0612.carpool.data.vehicle.datasource.VehicleStorageDataSource
import com.juanpablo0612.carpool.data.vehicle.repository.VehicleRepositoryImpl
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.vehicle.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val vehicleModule = module {
    singleOf(::FirebaseVehicleRemoteDataSource) bind VehicleRemoteDataSource::class
    singleOf(::FirebaseVehicleStorageDataSource) bind VehicleStorageDataSource::class
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    viewModel { (vehicleId: String?) ->
        RegisterVehicleViewModel(vehicleId, get(), get())
    }
    viewModel { VehiclesListViewModel(get(), get(), get()) }
}
