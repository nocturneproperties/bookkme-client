package library.files.impl.preferences

import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.files.api.FileProvider


internal class PreferenceProviderImpl(
    private val fileProvider: FileProvider
) : PreferenceProvider {
    override fun get(fileName: String): Preferences {
        return PreferencesImpl(fileName, fileProvider)
    }
}