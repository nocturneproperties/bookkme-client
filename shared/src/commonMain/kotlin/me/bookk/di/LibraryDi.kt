package me.bookk.di

import library.cache.impl.di.cacheModule
import library.credentials.impl.di.credentialsModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import library.money.impl.di.moneyModule
import library.permissions.impl.di.permissionsModule
import org.koin.dsl.module

internal fun libraryModule() = module {
    includes(
        filesModule(),
        permissionsModule(),
        cacheModule(),
        deviceModule(),
        credentialsModule(),
        moneyModule()
    )
}