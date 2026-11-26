package me.bookk.client.di

import library.cache.impl.di.cacheModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import library.permissions.impl.di.permissionsModule
import library.validation.impl.di.validationModule
import org.koin.dsl.module

internal fun libraryModule() = module {
    includes(
        deviceModule(),
        crashReportingModule(),
        cacheModule(),
        filesModule(),
        permissionsModule(),
        validationModule(),
    )
}
