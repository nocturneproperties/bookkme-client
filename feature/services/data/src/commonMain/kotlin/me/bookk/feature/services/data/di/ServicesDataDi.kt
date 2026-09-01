package me.bookk.feature.services.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.services.data.datasource.QuoteDataSourceImpl
import me.bookk.feature.services.data.datasource.ServiceDataSourceImpl
import me.bookk.feature.services.data.datasource.ServiceGroupDataSourceImpl
import me.bookk.feature.services.domain.datasource.QuoteDataSource
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun servicesDataModule() = module {
    singleOf(::ServiceGroupDataSourceImpl) binds arrayOf(ServiceGroupDataSource::class, LogOutAction::class)
    singleOf(::ServiceDataSourceImpl) binds arrayOf(ServiceDataSource::class, LogOutAction::class)
    singleOf(::QuoteDataSourceImpl) bind QuoteDataSource::class
}