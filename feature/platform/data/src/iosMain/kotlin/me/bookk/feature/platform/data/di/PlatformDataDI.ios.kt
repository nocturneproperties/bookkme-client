package me.bookk.feature.platform.data.di

import me.bookk.feature.platform.data.IosFileProvider
import me.bookk.feature.platform.data.IosPlatformInterface
import me.bookk.feature.platform.domain.datasource.FileProvider
import me.bookk.feature.platform.domain.datasource.PlatformInterface
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformDataPlatformModule(): Module = module {
    factoryOf(::IosPlatformInterface) bind PlatformInterface::class
    factoryOf(::IosFileProvider) bind FileProvider::class
}