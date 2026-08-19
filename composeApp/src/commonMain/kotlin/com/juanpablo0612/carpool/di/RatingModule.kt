package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.rating.datasource.FirebaseRatingRemoteDataSource
import com.juanpablo0612.carpool.data.rating.datasource.RatingRemoteDataSource
import com.juanpablo0612.carpool.data.rating.repository.RatingRepositoryImpl
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import com.juanpablo0612.carpool.domain.rating.usecase.CreateRatingUseCase
import com.juanpablo0612.carpool.presentation.rating.RatingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val ratingModule = module {
    singleOf(::FirebaseRatingRemoteDataSource) bind RatingRemoteDataSource::class
    singleOf(::RatingRepositoryImpl) bind RatingRepository::class
    factoryOf(::CreateRatingUseCase)
    viewModel { (bookingId: String, tripId: String, rateeId: String, rateeName: String, rateeIsDriver: Boolean) ->
        RatingViewModel(bookingId, tripId, rateeId, rateeName, rateeIsDriver, get(), get())
    }
}
