package library.cache.impl.di

import library.cache.impl.preferences.PreferenceDataStoreProducer
import library.cache.impl.preferences.WebPreferenceDataStoreProducer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun preferenceStorageModule(): Module = module {
    singleOf(::WebPreferenceDataStoreProducer) bind PreferenceDataStoreProducer::class
}
