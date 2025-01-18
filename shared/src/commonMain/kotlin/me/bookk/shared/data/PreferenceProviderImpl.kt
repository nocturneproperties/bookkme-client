package me.bookk.shared.data

import me.bookk.core.storage.FileProvider
import me.bookk.core.storage.PreferenceProvider
import me.bookk.core.storage.Preferences

class PreferenceProviderImpl(
    private val fileProvider: FileProvider
) : PreferenceProvider {
    override fun get(fileName: String): Preferences {
        return PreferencesImpl(fileName, fileProvider)
    }
}