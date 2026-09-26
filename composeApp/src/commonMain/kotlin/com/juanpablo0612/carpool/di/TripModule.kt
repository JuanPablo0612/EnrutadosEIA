package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.trip.datasource.FirebaseTripRemoteDataSource
import com.juanpablo0612.carpool.data.trip.datasource.TripRemoteDataSource
import com.juanpablo0612.carpool.data.trip.repository.TripRepositoryImpl
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.usecase.GenerateRecurringTripSlotsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.MatchTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.PublishRecurringTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.PublishTripUseCase
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.trip.publish.PublishTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val tripModule = module {
    singleOf(::FirebaseTripRemoteDataSource) bind TripRemoteDataSource::class
    singleOf(::TripRepositoryImpl) bind TripRepository::class
    factoryOf(::GetAvailableTripsUseCase)
    factoryOf(::MatchTripsUseCase)
    factoryOf(::GenerateRecurringTripSlotsUseCase)
    factoryOf(::PublishTripUseCase)
    factoryOf(::PublishRecurringTripsUseCase)
    // SearchRoutesViewModel lives in presentation/route/search/ but depends on trip matching and
    // trip data, so it is registered with the trip feature.
    viewModel { SearchRoutesViewModel(get(), get(), get(), get(), get()) }
    viewModel { (routeId: String?) -> PublishTripViewModel(routeId, get(), get(), get(), get(), get(), get()) }
    viewModel { DriverTripsViewModel(get(), get(), get(), get()) }
    viewModel { (tripId: String) -> RouteDetailPassengerViewModel(tripId, get(), get(), get(), get(), get(), get(), get()) }
    viewModel { (tripId: String) -> TripTrackingViewModel(tripId, get(), get(), get(), get(), get()) }
}
