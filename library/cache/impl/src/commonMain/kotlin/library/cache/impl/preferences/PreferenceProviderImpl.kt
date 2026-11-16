package library.cache.impl.preferences

import library.cache.api.PreferenceProvider
import library.cache.api.Preferences

internal class PreferenceProviderImpl(
    private val dataStoreProducer: PreferenceDataStoreProducer,
) : PreferenceProvider {
    override fun get(fileName: String): Preferences {
        return PreferencesImpl(dataStoreProducer.produce(fileName))
    }
}
