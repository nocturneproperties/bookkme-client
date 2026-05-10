package me.bookk.feature.services.data.di

import me.bookk.feature.services.data.datasource.ServiceDataSourceImpl
import me.bookk.feature.services.data.datasource.ServiceGroupDataSourceImpl
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun servicesDataModule() = module {
    singleOf(::ServiceGroupDataSourceImpl) bind ServiceGroupDataSource::class
    singleOf(::ServiceDataSourceImpl) bind ServiceDataSource::class
}