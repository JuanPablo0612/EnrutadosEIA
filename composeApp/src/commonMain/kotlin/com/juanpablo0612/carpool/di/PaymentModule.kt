package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.payment.datasource.FirebasePaymentMethodRemoteDataSource
import com.juanpablo0612.carpool.data.payment.datasource.GalleryDataSource
import com.juanpablo0612.carpool.data.payment.datasource.PaymentMethodRemoteDataSource
import com.juanpablo0612.carpool.data.payment.repository.PaymentMethodRepositoryImpl
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.payment.usecase.AddPaymentMethodUseCase
import com.juanpablo0612.carpool.presentation.payment.manage.PaymentMethodsViewModel
import com.juanpablo0612.carpool.presentation.payment.pay.PayDriverViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val paymentModule = module {
    singleOf(::FirebasePaymentMethodRemoteDataSource) bind PaymentMethodRemoteDataSource::class
    // Its own plain client: the Places one carries JSON negotiation and short timeouts for an API.
    single { GalleryDataSource(HttpClient()) }
    singleOf(::PaymentMethodRepositoryImpl) bind PaymentMethodRepository::class
    factoryOf(::AddPaymentMethodUseCase)
    viewModel { PaymentMethodsViewModel(get(), get(), get()) }
    viewModel { (tripId: String) -> PayDriverViewModel(tripId, get(), get()) }
}
