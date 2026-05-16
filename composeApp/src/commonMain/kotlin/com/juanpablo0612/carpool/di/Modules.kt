package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.remote.FirebaseAuthRemoteDataSource
import com.juanpablo0612.carpool.data.auth.repository.AuthRepositoryImpl
import com.juanpablo0612.carpool.data.booking.repository.BookingRepositoryImpl
import com.juanpablo0612.carpool.data.chat.ChatRepositoryImpl
import com.juanpablo0612.carpool.data.notification.NotificationRepositoryImpl
import com.juanpablo0612.carpool.data.places.repository.PlacesRepositoryImpl
import com.juanpablo0612.carpool.data.preferences.UserPreferencesDataSource
import com.juanpablo0612.carpool.data.preferences.UserPreferencesRepositoryImpl
import com.juanpablo0612.carpool.data.rating.RatingRepositoryImpl
import com.juanpablo0612.carpool.data.routes.repository.RouteRepositoryImpl
import com.juanpablo0612.carpool.data.safety.SafetyRepositoryImpl
import com.juanpablo0612.carpool.data.trip.repository.TripRepositoryImpl
import com.juanpablo0612.carpool.data.vehicles.repository.VehicleRepositoryImpl
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.use_case.DeleteAccountUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.LogoutUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.SendEmailVerificationUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.UpdateProfileUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.UpdateUserRolesUseCase
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.use_case.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetAllDriverBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetPassengerBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import com.juanpablo0612.carpool.domain.chat.use_case.GetMessagesUseCase
import com.juanpablo0612.carpool.domain.chat.use_case.MarkMessagesReadUseCase
import com.juanpablo0612.carpool.domain.chat.use_case.SendMessageUseCase
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.domain.notification.use_case.ClearAllNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.CreateNotificationUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.DeleteNotificationUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.GetNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.MarkNotificationReadUseCase
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.places.use_case.SearchPlacesUseCase
import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository
import com.juanpablo0612.carpool.domain.preferences.use_case.ClearRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.GetOnboardingSeenUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.GetRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.SaveRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.SetOnboardingSeenUseCase
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import com.juanpablo0612.carpool.domain.rating.use_case.CreateRatingUseCase
import com.juanpablo0612.carpool.domain.rating.use_case.GetUserAverageRatingUseCase
import com.juanpablo0612.carpool.domain.rating.use_case.HasRatedBookingUseCase
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import com.juanpablo0612.carpool.domain.routes.use_case.CreateRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.DeleteRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetUserRoutesUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.UpdateRouteUseCase
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository
import com.juanpablo0612.carpool.domain.safety.use_case.AddEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.GetEmergencyContactsUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.GetSafetySettingsUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.RemoveEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.UpdateSafetySettingsUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.use_case.CreateTripUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdFlowUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateDriverLocationUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdatePassengerStatusUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateTripStatusUseCase
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import com.juanpablo0612.carpool.domain.vehicles.use_case.CreateVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.DeleteVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetDriverVehiclesUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetVehicleByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.SetPrimaryVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.UpdateVehicleUseCase
import com.juanpablo0612.carpool.presentation.auth.email_verification.EmailVerificationViewModel
import com.juanpablo0612.carpool.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.juanpablo0612.carpool.presentation.auth.login.LoginViewModel
import com.juanpablo0612.carpool.presentation.auth.register.RegisterViewModel
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.bookings.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.chat.ChatViewModel
import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import com.juanpablo0612.carpool.presentation.notifications.NotificationsViewModel
import com.juanpablo0612.carpool.presentation.onboarding.OnboardingViewModel
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.profile.ProfileViewModel
import com.juanpablo0612.carpool.presentation.profile.edit.EditProfileViewModel
import com.juanpablo0612.carpool.presentation.rating.RatingViewModel
import com.juanpablo0612.carpool.presentation.role_selector.RoleSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.routes.passenger_detail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.safety.SafetyViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.splash.SplashViewModel
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingViewModel
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
    factoryOf(::SendEmailVerificationUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::UpdateUserRolesUseCase)
    factoryOf(::DeleteAccountUseCase)

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get()) }
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
    viewModel { RoleSelectorViewModel(get(), get(), get(), get()) }
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
    viewModel { SearchRoutesViewModel(get(), get(), get()) }
    viewModel { (routeId: String) -> CreateTripViewModel(routeId, get(), get(), get(), get()) }
    viewModel { DriverTripsViewModel(get(), get(), get(), get(), get()) }
    viewModel { (tripId: String) -> RouteDetailPassengerViewModel(tripId, get(), get(), get(), get(), get()) }
    viewModel { (tripId: String) -> TripTrackingViewModel(tripId, get(), get(), get(), get(), get()) }
}

val placeModule = module {
    singleOf(::PlacesRepositoryImpl) bind PlacesRepository::class
    factoryOf(::GetSavedPlacesUseCase)
    factoryOf(::SearchPlacesUseCase)
    factoryOf(::CreatePlaceUseCase)
    viewModel { (mode: String) -> PlaceSelectorViewModel(mode, get(), get(), get()) }
    viewModel { AddPlaceViewModel(get(), get()) }
}

val vehicleModule = module {
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    factoryOf(::CreateVehicleUseCase)
    factoryOf(::UpdateVehicleUseCase)
    factoryOf(::DeleteVehicleUseCase)
    factoryOf(::SetPrimaryVehicleUseCase)
    factoryOf(::GetUserVehiclesUseCase)
    factoryOf(::GetDriverVehiclesUseCase)
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
        HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
}

val ratingModule = module {
    singleOf(::RatingRepositoryImpl) bind RatingRepository::class
    factoryOf(::CreateRatingUseCase)
    factoryOf(::HasRatedBookingUseCase)
    factoryOf(::GetUserAverageRatingUseCase)
    viewModel { (bookingId: String, tripId: String, rateeId: String, rateeName: String, isDriver: Boolean) ->
        RatingViewModel(bookingId, tripId, rateeId, rateeName, isDriver, get(), get())
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
