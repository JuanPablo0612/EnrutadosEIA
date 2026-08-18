package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.remote.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.data.booking.repository.BookingRepositoryImpl
import com.juanpablo0612.carpool.data.chat.ChatRepositoryImpl
import com.juanpablo0612.carpool.data.notification.NotificationRepositoryImpl
import com.juanpablo0612.carpool.data.place.repository.PlaceRepositoryImpl
import com.juanpablo0612.carpool.data.place.service.CompassLocationService
import com.juanpablo0612.carpool.data.place.service.CompassPlacesSearchService
import com.juanpablo0612.carpool.data.preferences.UserPreferencesDataSource
import com.juanpablo0612.carpool.data.preferences.UserPreferencesRepositoryImpl
import com.juanpablo0612.carpool.data.rating.RatingRepositoryImpl
import com.juanpablo0612.carpool.data.route.repository.RouteRepositoryImpl
import com.juanpablo0612.carpool.data.safety.SafetyRepositoryImpl
import com.juanpablo0612.carpool.data.trip.repository.TripRepositoryImpl
import com.juanpablo0612.carpool.data.vehicle.repository.VehicleRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.usecase.DeleteAccountUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.GetUserPublicProfileUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.LogoutUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.RegisterUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.SendEmailVerificationUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.UpdateProfileUseCase
import com.juanpablo0612.carpool.domain.auth.usecase.UpdateUserRolesUseCase
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetAllDriverBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetPassengerBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import com.juanpablo0612.carpool.domain.chat.usecase.GetMessagesUseCase
import com.juanpablo0612.carpool.domain.chat.usecase.MarkMessagesReadUseCase
import com.juanpablo0612.carpool.domain.chat.usecase.SendMessageUseCase
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.domain.notification.usecase.ClearAllNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.usecase.CreateNotificationUseCase
import com.juanpablo0612.carpool.domain.notification.usecase.DeleteNotificationUseCase
import com.juanpablo0612.carpool.domain.notification.usecase.GetNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.usecase.MarkNotificationReadUseCase
import com.juanpablo0612.carpool.domain.place.repository.PlaceRepository
import com.juanpablo0612.carpool.domain.place.service.LocationService
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService
import com.juanpablo0612.carpool.domain.place.usecase.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.place.usecase.DeletePlaceUseCase
import com.juanpablo0612.carpool.domain.place.usecase.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository
import com.juanpablo0612.carpool.domain.preferences.usecase.ClearRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.usecase.GetOnboardingSeenUseCase
import com.juanpablo0612.carpool.domain.preferences.usecase.GetRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.usecase.SaveRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.usecase.SetOnboardingSeenUseCase
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import com.juanpablo0612.carpool.domain.rating.usecase.CreateRatingUseCase
import com.juanpablo0612.carpool.domain.rating.usecase.GetUserAverageRatingUseCase
import com.juanpablo0612.carpool.domain.rating.usecase.HasRatedBookingUseCase
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.route.usecase.CreateRouteUseCase
import com.juanpablo0612.carpool.domain.route.usecase.DeleteRouteUseCase
import com.juanpablo0612.carpool.domain.route.usecase.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.route.usecase.GetUserRoutesUseCase
import com.juanpablo0612.carpool.domain.route.usecase.UpdateRouteUseCase
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository
import com.juanpablo0612.carpool.domain.safety.usecase.AddEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.usecase.GetEmergencyContactsUseCase
import com.juanpablo0612.carpool.domain.safety.usecase.GetSafetySettingsUseCase
import com.juanpablo0612.carpool.domain.safety.usecase.RemoveEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.usecase.UpdateSafetySettingsUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.usecase.CreateTripUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetTripByIdFlowUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.UpdateDriverLocationUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.UpdatePassengerStatusUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.UpdateTripStatusUseCase
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.domain.vehicle.usecase.CreateVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.DeleteVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.GetVehicleByIdUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.SetPrimaryVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.UpdateVehicleUseCase
import com.juanpablo0612.carpool.presentation.auth.emailverification.EmailVerificationViewModel
import com.juanpablo0612.carpool.presentation.auth.forgotpassword.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.booking.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.chat.ChatViewModel
import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import com.juanpablo0612.carpool.presentation.notification.NotificationsViewModel
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingViewModel
import com.juanpablo0612.carpool.presentation.place.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.place.add.components.createLocationPermissionRequester
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerViewModel
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.profile.ProfileViewModel
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileViewModel
import com.juanpablo0612.carpool.presentation.rating.RatingViewModel
import com.juanpablo0612.carpool.presentation.roleselector.RoleSelectorViewModel
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.route.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.safety.SafetyViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingViewModel
import com.juanpablo0612.carpool.presentation.vehicle.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleViewModel
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
    factoryOf(::SendEmailVerificationUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::GetUserPublicProfileUseCase)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::UpdateUserRolesUseCase)
    factoryOf(::DeleteAccountUseCase)

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { EmailVerificationViewModel(get(), get()) }
    viewModel { SplashViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get()) }
}

val preferencesModule = module {
    // DataStore singleton is provided by platformModule (platform-specific)
    singleOf(::UserPreferencesDataSource)
    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
    factoryOf(::SaveRolePreferenceUseCase)
    factoryOf(::GetRolePreferenceUseCase)
    factoryOf(::ClearRolePreferenceUseCase)
    factoryOf(::GetOnboardingSeenUseCase)
    factoryOf(::SetOnboardingSeenUseCase)
    viewModel { OnboardingViewModel(get()) }
}

val roleSelectorModule = module {
    viewModel { RoleSelectorViewModel(get(), get(), get(), get(), get()) }
}

val routeModule = module {
    singleOf(::RouteRepositoryImpl) bind RouteRepository::class
    factoryOf(::CreateRouteUseCase)
    factoryOf(::GetUserRoutesUseCase)
    factoryOf(::GetRouteByIdUseCase)
    factoryOf(::UpdateRouteUseCase)
    factoryOf(::DeleteRouteUseCase)
    viewModel { CreateRouteViewModel(get(), get()) }
    viewModel { RoutesListViewModel(get(), get(), get(), get()) }
    viewModel { (routeId: String) -> RouteDetailViewModel(routeId, get(), get(), get(), get(), get()) }
}

val tripModule = module {
    singleOf(::TripRepositoryImpl) bind TripRepository::class
    factoryOf(::CreateTripUseCase)
    factoryOf(::GetDriverTripsUseCase)
    factoryOf(::GetAvailableTripsUseCase)
    factoryOf(::GetTripByIdUseCase)
    factoryOf(::GetTripByIdFlowUseCase)
    factoryOf(::UpdateTripStatusUseCase)
    factoryOf(::UpdateDriverLocationUseCase)
    factoryOf(::UpdatePassengerStatusUseCase)
    viewModel { SearchRoutesViewModel(get(), get(), get(), get()) }
    viewModel { (routeId: String) -> CreateTripViewModel(routeId, get(), get(), get(), get()) }
    viewModel { DriverTripsViewModel(get(), get(), get(), get(), get()) }
    viewModel { (tripId: String) -> RouteDetailPassengerViewModel(tripId, get(), get(), get(), get(), get(), get()) }
    viewModel { (tripId: String) -> TripTrackingViewModel(tripId, get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}

val placeModule = module {
    singleOf(::CompassLocationService) bind LocationService::class
    singleOf(::CompassPlacesSearchService) bind PlacesSearchService::class
    singleOf(::PlaceRepositoryImpl) bind PlaceRepository::class
    single { createLocationPermissionRequester() }
    factoryOf(::GetSavedPlacesUseCase)
    factoryOf(::CreatePlaceUseCase)
    factoryOf(::DeletePlaceUseCase)
    viewModel { (mode: String) -> PlaceSelectorViewModel(mode, get(), get(), get(), get(), get()) }
    viewModel { AddPlaceViewModel(get(), get()) }
    viewModel { (lat: Double, lon: Double) -> MapPickerViewModel(lat, lon, get(), get()) }
}

val vehicleModule = module {
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    factoryOf(::CreateVehicleUseCase)
    factoryOf(::UpdateVehicleUseCase)
    factoryOf(::DeleteVehicleUseCase)
    factoryOf(::SetPrimaryVehicleUseCase)
    factoryOf(::GetUserVehiclesUseCase)
    factoryOf(::GetVehicleByIdUseCase)
    viewModel { (vehicleId: String?) ->
        RegisterVehicleViewModel(vehicleId, get(), get(), get(), get())
    }
    viewModel { VehiclesListViewModel(get(), get(), get(), get(), get()) }
}

val bookingModule = module {
    singleOf(::BookingRepositoryImpl) bind BookingRepository::class
    factoryOf(::CreateBookingUseCase)
    factoryOf(::GetTripAvailableSeatsUseCase)
    factoryOf(::GetPassengerBookingsUseCase)
    factoryOf(::GetDriverBookingRequestsUseCase)
    factoryOf(::GetAllDriverBookingsUseCase)
    factoryOf(::GetBookingsForTripUseCase)
    factoryOf(::ConfirmBookingUseCase)
    factoryOf(::RejectBookingUseCase)
    factoryOf(::CancelBookingUseCase)
    factoryOf(::CheckExistingBookingUseCase)
    viewModel { PassengerBookingsViewModel(get(), get(), get()) }
    viewModel { BookingRequestsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}

val homeModule = module {
    viewModel {
        HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}

val ratingModule = module {
    singleOf(::RatingRepositoryImpl) bind RatingRepository::class
    factoryOf(::CreateRatingUseCase)
    factoryOf(::HasRatedBookingUseCase)
    factoryOf(::GetUserAverageRatingUseCase)
    viewModel { (bookingId: String, tripId: String, rateeId: String, rateeName: String, rateeIsDriver: Boolean) ->
        RatingViewModel(bookingId, tripId, rateeId, rateeName, rateeIsDriver, get(), get())
    }
}

val chatModule = module {
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    factoryOf(::GetMessagesUseCase)
    factoryOf(::SendMessageUseCase)
    factoryOf(::MarkMessagesReadUseCase)
    viewModel { (bookingId: String, otherPartyName: String, isReadOnly: Boolean) ->
        ChatViewModel(bookingId, otherPartyName, isReadOnly, get(), get(), get(), get())
    }
}

val notificationModule = module {
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
    factoryOf(::GetNotificationsUseCase)
    factoryOf(::CreateNotificationUseCase)
    factoryOf(::MarkNotificationReadUseCase)
    factoryOf(::DeleteNotificationUseCase)
    factoryOf(::ClearAllNotificationsUseCase)
    viewModel { NotificationsViewModel(get(), get(), get(), get(), get()) }
}

val safetyModule = module {
    singleOf(::SafetyRepositoryImpl) bind SafetyRepository::class
    factoryOf(::GetEmergencyContactsUseCase)
    factoryOf(::AddEmergencyContactUseCase)
    factoryOf(::RemoveEmergencyContactUseCase)
    factoryOf(::GetSafetySettingsUseCase)
    factoryOf(::UpdateSafetySettingsUseCase)
    viewModel { SafetyViewModel(get(), get(), get(), get(), get(), get()) }
}

val appModule = module {
    includes(
        platformModule,
        authModule,
        preferencesModule,
        roleSelectorModule,
        routeModule,
        tripModule,
        placeModule,
        vehicleModule,
        bookingModule,
        homeModule,
        ratingModule,
        chatModule,
        notificationModule,
        safetyModule
    )
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
