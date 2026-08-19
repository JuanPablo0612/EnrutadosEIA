package com.juanpablo0612.carpool.di

import com.juanpablo0612.carpool.data.route.datasource.FirebaseRouteRemoteDataSource
import com.juanpablo0612.carpool.data.route.datasource.RouteRemoteDataSource
import com.juanpablo0612.carpool.data.route.repository.RouteRepositoryImpl
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.route.list.RoutesListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val routeModule = module {
    singleOf(::FirebaseRouteRemoteDataSource) bind RouteRemoteDataSource::class
    singleOf(::RouteRepositoryImpl) bind RouteRepository::class
    viewModel { CreateRouteViewModel(get(), get()) }
    viewModel { RoutesListViewModel(get(), get(), get()) }
    viewModel { (routeId: String) -> RouteDetailViewModel(routeId, get(), get()) }
}
