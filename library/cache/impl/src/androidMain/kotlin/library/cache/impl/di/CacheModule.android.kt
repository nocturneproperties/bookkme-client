package library.cache.impl.di

import library.cache.impl.preferences.FilePreferenceDataStoreProducer
import library.cache.impl.preferences.PreferenceDataStoreProducer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun preferenceStorageModule(): Module = module {
    singleOf(::FilePreferenceDataStoreProducer) bind PreferenceDataStoreProducer::class
}
