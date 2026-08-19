package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.booking.datasource.BookingRemoteDataSource
import com.juanpablo0612.carpool.data.booking.datasource.FirebaseBookingRemoteDataSource
import com.juanpablo0612.carpool.data.booking.repository.BookingRepositoryImpl
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.presentation.booking.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val bookingModule = module {
    singleOf(::FirebaseBookingRemoteDataSource) bind BookingRemoteDataSource::class
    singleOf(::BookingRepositoryImpl) bind BookingRepository::class
    factoryOf(::CreateBookingUseCase)
    factoryOf(::GetTripAvailableSeatsUseCase)
    factoryOf(::GetBookingsForTripUseCase)
    factoryOf(::ConfirmBookingUseCase)
    factoryOf(::RejectBookingUseCase)
    factoryOf(::CancelBookingUseCase)
    factoryOf(::CheckExistingBookingUseCase)
    viewModel { PassengerBookingsViewModel(get(), get(), get()) }
    viewModel { BookingRequestsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
