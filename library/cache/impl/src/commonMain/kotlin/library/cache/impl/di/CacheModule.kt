package library.cache.impl.di

import library.cache.api.PreferenceProvider
import library.cache.impl.preferences.PreferenceProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun preferenceStorageModule(): Module

fun cacheModule() = module {
    includes(preferenceStorageModule())
    singleOf(::PreferenceProviderImpl) bind PreferenceProvider::class
}
