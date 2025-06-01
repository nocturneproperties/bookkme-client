package library.cache.impl

import library.cache.api.FileProvider
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences


internal class PreferenceProviderImpl(
    private val fileProvider: FileProvider
) : PreferenceProvider {
    override fun get(fileName: String): Preferences {
        return PreferencesImpl(fileName, fileProvider)
    }
}