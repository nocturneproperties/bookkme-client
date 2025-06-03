package library.cache.impl.di

import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.impl.PreferenceProviderImpl
import library.cache.impl.PreferencesImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun cacheModule() = module {
    singleOf(::PreferencesImpl) bind Preferences::class
    singleOf(::PreferenceProviderImpl) bind PreferenceProvider::class
}