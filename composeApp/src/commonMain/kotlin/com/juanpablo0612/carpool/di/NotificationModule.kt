package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.notification.datasource.FirebaseNotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.datasource.NotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.repository.NotificationRepositoryImpl
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.domain.notification.usecase.CreateNotificationUseCase
import com.juanpablo0612.carpool.presentation.notification.NotificationsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val notificationModule = module {
    singleOf(::FirebaseNotificationRemoteDataSource) bind NotificationRemoteDataSource::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
    factoryOf(::CreateNotificationUseCase)
    viewModel { NotificationsViewModel(get(), get()) }
}
