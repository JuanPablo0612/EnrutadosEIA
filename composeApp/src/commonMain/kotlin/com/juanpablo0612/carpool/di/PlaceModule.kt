package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.place.datasource.CompassLocationService
import com.juanpablo0612.carpool.data.place.datasource.CompassPlacesSearchService
import com.juanpablo0612.carpool.data.place.datasource.FirebasePlaceRemoteDataSource
import com.juanpablo0612.carpool.data.place.datasource.PlaceRemoteDataSource
import com.juanpablo0612.carpool.data.place.repository.PlaceRepositoryImpl
import com.juanpablo0612.carpool.domain.place.repository.PlaceRepository
import com.juanpablo0612.carpool.domain.place.service.LocationService
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService
import com.juanpablo0612.carpool.domain.place.usecase.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.place.usecase.DeletePlaceUseCase
import com.juanpablo0612.carpool.domain.place.usecase.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.presentation.place.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerViewModel
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val placeModule = module {
    singleOf(::CompassLocationService) bind LocationService::class
    singleOf(::CompassPlacesSearchService) bind PlacesSearchService::class
    singleOf(::FirebasePlaceRemoteDataSource) bind PlaceRemoteDataSource::class
    singleOf(::PlaceRepositoryImpl) bind PlaceRepository::class
    factoryOf(::GetSavedPlacesUseCase)
    factoryOf(::CreatePlaceUseCase)
    factoryOf(::DeletePlaceUseCase)
    viewModel { (mode: String) -> PlaceSelectorViewModel(mode, get(), get(), get(), get(), get()) }
    viewModel { AddPlaceViewModel(get(), get()) }
    viewModel { (lat: Double, lon: Double) -> MapPickerViewModel(lat, lon, get(), get()) }
}
