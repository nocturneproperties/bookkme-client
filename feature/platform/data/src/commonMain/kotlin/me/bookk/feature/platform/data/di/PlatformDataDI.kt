package me.bookk.feature.platform.data.di

import me.bookk.feature.platform.data.PreferenceProviderImpl
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun platformDataPlatformModule(): Module

fun platformDataModule() = module {
    includes(platformDataPlatformModule())
    singleOf(::PreferenceProviderImpl) bind PreferenceProvider::class
}