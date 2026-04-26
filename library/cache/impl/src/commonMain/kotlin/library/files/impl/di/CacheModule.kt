package library.files.impl.di

import library.cache.api.Cache
import library.cache.api.CacheProvider
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.files.impl.cache.CacheProviderImpl
import library.files.impl.cache.TtlCacheImpl
import library.files.impl.preferences.PreferenceProviderImpl
import library.files.impl.preferences.PreferencesImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun cacheModule() = module {
    factoryOf(::PreferencesImpl) bind Preferences::class
    singleOf(::PreferenceProviderImpl) bind PreferenceProvider::class
    factoryOf(::TtlCacheImpl) bind Cache::class
    singleOf(::CacheProviderImpl) bind CacheProvider::class
}