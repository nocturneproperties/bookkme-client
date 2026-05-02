package me.bookk.feature.business.data.di

import me.bookk.core.data.mock.RoutingMock
import me.bookk.feature.business.data.datasource.CommonBusinessDataSource
import me.bookk.feature.business.data.datasource.CommonClientsDataSource
import me.bookk.feature.business.data.remote.mock.BusinessRoutingMock
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun businessDataModule() = module {
    singleOf(::CommonBusinessDataSource) bind BusinessDataSource::class
    singleOf(::CommonClientsDataSource) bind CommonClientsDataSource::class
    singleOf(::BusinessRoutingMock) bind RoutingMock::class
}