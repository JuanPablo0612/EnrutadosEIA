package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.remote.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.data.booking.repository.BookingRepositoryImpl
import com.juanpablo0612.carpool.data.places.repository.PlacesRepositoryImpl
import com.juanpablo0612.carpool.data.routes.repository.RouteRepositoryImpl
import com.juanpablo0612.carpool.data.trip.repository.TripRepositoryImpl
import com.juanpablo0612.carpool.data.vehicles.repository.VehicleRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.use_case.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.use_case.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetPassengerBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.places.use_case.SearchPlacesUseCase
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import com.juanpablo0612.carpool.domain.routes.use_case.CreateRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetUserRoutesUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.UpdateRouteUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.use_case.CreateTripUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import com.juanpablo0612.carpool.domain.vehicles.use_case.CreateVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetDriverVehiclesUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.bookings.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.routes.passenger_detail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.vehicles.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
    singleOf(::FirebaseAuthRemoteDataSource) bind AuthRemoteDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::UserSession)

    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::SendPasswordResetEmailUseCase)
    factoryOf(::GetCurrentUserUseCase)

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
}

val routeModule = module {
    singleOf(::RouteRepositoryImpl) bind RouteRepository::class
    factoryOf(::CreateRouteUseCase)
    factoryOf(::GetUserRoutesUseCase)
    factoryOf(::GetRouteByIdUseCase)
    factoryOf(::UpdateRouteUseCase)
    viewModel { CreateRouteViewModel(get(), get()) }
    viewModel { RoutesListViewModel(get(), get()) }
    viewModel { (routeId: String) -> RouteDetailViewModel(routeId, get(), get()) }
}

val tripModule = module {
    singleOf(::TripRepositoryImpl) bind TripRepository::class
    factoryOf(::CreateTripUseCase)
    factoryOf(::GetDriverTripsUseCase)
    factoryOf(::GetAvailableTripsUseCase)
    factoryOf(::GetTripByIdUseCase)
    viewModel { SearchRoutesViewModel(get()) }
    viewModel { (routeId: String) -> CreateTripViewModel(routeId, get(), get(), get(), get()) }
    viewModel { DriverTripsViewModel(get(), get()) }
    viewModel { (tripId: String) -> RouteDetailPassengerViewModel(tripId, get(), get(), get(), get()) }
}

val placeModule = module {
    singleOf(::PlacesRepositoryImpl) bind PlacesRepository::class
    factoryOf(::GetSavedPlacesUseCase)
    factoryOf(::SearchPlacesUseCase)
    factoryOf(::CreatePlaceUseCase)
    viewModel { PlaceSelectorViewModel(get(), get()) }
    viewModel { AddPlaceViewModel(get()) }
}

val vehicleModule = module {
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    factoryOf(::CreateVehicleUseCase)
    factoryOf(::GetUserVehiclesUseCase)
    factoryOf(::GetDriverVehiclesUseCase)
    viewModel { RegisterVehicleViewModel(get(), get()) }
    viewModel { VehiclesListViewModel(get(), get()) }
}

val bookingModule = module {
    singleOf(::BookingRepositoryImpl) bind BookingRepository::class
    factoryOf(::CreateBookingUseCase)
    factoryOf(::GetTripAvailableSeatsUseCase)
    factoryOf(::GetPassengerBookingsUseCase)
    factoryOf(::GetDriverBookingRequestsUseCase)
    factoryOf(::ConfirmBookingUseCase)
    factoryOf(::RejectBookingUseCase)
    factoryOf(::CancelBookingUseCase)
    viewModel { PassengerBookingsViewModel(get(), get(), get()) }
    viewModel { BookingRequestsViewModel(get(), get(), get(), get()) }
}

val appModule = module {
    includes(authModule, routeModule, tripModule, placeModule, vehicleModule, bookingModule)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
