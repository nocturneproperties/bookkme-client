package me.bookk.feature.clients.data.di

import me.bookk.feature.clients.data.datasource.AndroidDeviceContactsDataSource
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun clientsDataPlatformModule(): Module = module {
    singleOf(::AndroidDeviceContactsDataSource) bind DeviceContactsDataSource::class
}
