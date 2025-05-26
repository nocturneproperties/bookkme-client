package me.bookk.feature.business.data.di

import me.bookk.feature.business.data.datasource.CommonBusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun businessDataModule() = module {
    singleOf(::CommonBusinessDataSource) bind BusinessDataSource::class
}