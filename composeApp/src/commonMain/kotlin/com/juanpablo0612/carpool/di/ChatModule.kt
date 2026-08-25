package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.chat.datasource.ChatRemoteDataSource
import com.juanpablo0612.carpool.data.chat.datasource.FirebaseChatRemoteDataSource
import com.juanpablo0612.carpool.data.chat.repository.ChatRepositoryImpl
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import com.juanpablo0612.carpool.domain.chat.usecase.SendMessageUseCase
import com.juanpablo0612.carpool.presentation.chat.ChatViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val chatModule = module {
    singleOf(::FirebaseChatRemoteDataSource) bind ChatRemoteDataSource::class
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    factoryOf(::SendMessageUseCase)
    viewModel { (bookingId: String, otherPartyName: String, isReadOnly: Boolean) ->
        ChatViewModel(bookingId, otherPartyName, isReadOnly, get(), get(), get())
    }
}
