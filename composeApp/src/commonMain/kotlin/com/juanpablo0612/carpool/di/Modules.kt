package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.remote.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.data.places.repository.PlacesRepositoryImpl
import com.juanpablo0612.carpool.data.routes.repository.RouteRepositoryImpl
import com.juanpablo0612.carpool.data.vehicles.repository.VehicleRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.use_case.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.places.use_case.SearchPlacesUseCase
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import com.juanpablo0612.carpool.domain.routes.use_case.CreateRouteUseCase
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import com.juanpablo0612.carpool.domain.vehicles.use_case.CreateVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
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

    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::SendPasswordResetEmailUseCase)

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
}

val routeModule = module {
    singleOf(::RouteRepositoryImpl) bind RouteRepository::class
    factoryOf(::CreateRouteUseCase)
    viewModel { CreateRouteViewModel(get(), get()) }
}

val placeModule = module {
    singleOf(::PlacesRepositoryImpl) bind PlacesRepository::class
    factoryOf(::GetSavedPlacesUseCase)
    factoryOf(::SearchPlacesUseCase)
    factoryOf(::CreatePlaceUseCase)
    viewModel { PlaceSelectorViewModel(get(), get(), get()) }
}

val vehicleModule = module {
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    factoryOf(::CreateVehicleUseCase)
    factoryOf(::GetUserVehiclesUseCase)
    viewModel { RegisterVehicleViewModel(get(), get()) }
}

val appModule = module {
    includes(authModule, routeModule, placeModule, vehicleModule)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
