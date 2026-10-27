package me.bookk.feature.business.data.di

import me.bookk.core.data.mock.RoutingMock
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.business.data.datasource.CommonBusinessDataSource
import me.bookk.feature.business.data.datasource.CommonPluginDataSource
import me.bookk.feature.business.data.remote.mock.BusinessRoutingMock
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.PluginDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun businessDataModule() = module {
    singleOf(::CommonBusinessDataSource) binds arrayOf(BusinessDataSource::class, LogOutAction::class)
    singleOf(::BusinessRoutingMock) bind RoutingMock::class
    singleOf(::CommonPluginDataSource) binds arrayOf(PluginDataSource::class, LogOutAction::class)
}