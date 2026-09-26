package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.notification.datasource.FirebaseNotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.datasource.FirebasePushTokenRemoteDataSource
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenRemoteDataSource
import com.juanpablo0612.carpool.data.notification.datasource.PushTokenSync
import com.juanpablo0612.carpool.data.notification.datasource.NotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.repository.NotificationRepositoryImpl
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.presentation.navigation.PendingDeepLinks
import com.juanpablo0612.carpool.presentation.notification.NotificationsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val notificationModule = module {
    singleOf(::FirebaseNotificationRemoteDataSource) bind NotificationRemoteDataSource::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
    singleOf(::FirebasePushTokenRemoteDataSource) bind PushTokenRemoteDataSource::class
    singleOf(::PushTokenSync)
    singleOf(::PendingDeepLinks)
    viewModel { NotificationsViewModel(get(), get()) }
}
