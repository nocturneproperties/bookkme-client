package me.bookk.client.di

import library.cache.impl.di.cacheModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import org.koin.dsl.module

internal fun libraryModule() = module {
    includes(
        deviceModule(),
        cacheModule(),
        filesModule(),
    )
}
