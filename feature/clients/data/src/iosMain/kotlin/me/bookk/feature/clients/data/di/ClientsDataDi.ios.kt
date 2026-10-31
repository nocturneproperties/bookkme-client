package me.bookk.feature.clients.data.di

import me.bookk.feature.clients.data.datasource.IosDeviceContactsDataSource
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun clientsDataPlatformModule(): Module = module {
    singleOf(::IosDeviceContactsDataSource) bind DeviceContactsDataSource::class
}
