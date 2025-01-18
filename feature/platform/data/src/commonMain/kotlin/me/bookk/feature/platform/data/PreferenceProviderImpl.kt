package me.bookk.feature.platform.data

import me.bookk.feature.platform.domain.datasource.FileProvider
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences

class PreferenceProviderImpl(
    private val fileProvider: FileProvider
) : PreferenceProvider {
    override fun get(fileName: String): Preferences {
        return PreferencesImpl(fileName, fileProvider)
    }
}