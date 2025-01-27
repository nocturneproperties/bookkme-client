package me.bookk.feature.platform.data.di

import me.bookk.feature.platform.data.AndroidFileProvider
import me.bookk.feature.platform.data.AndroidPlatformInterface
import me.bookk.feature.platform.domain.datasource.FileProvider
import me.bookk.feature.platform.domain.datasource.PlatformInterface
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformDataPlatformModule(): Module = module {
    factoryOf(::AndroidPlatformInterface) bind PlatformInterface::class
    factoryOf(::AndroidFileProvider) bind FileProvider::class
}