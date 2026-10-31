package me.bookk.feature.clients.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.clients.data.datasource.CommonClientsDataSource
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal expect fun clientsDataPlatformModule(): Module

fun clientsDataModule() = module {
    includes(clientsDataPlatformModule())
    singleOf(::CommonClientsDataSource) binds arrayOf(ClientsDataSource::class, LogOutAction::class)
}
