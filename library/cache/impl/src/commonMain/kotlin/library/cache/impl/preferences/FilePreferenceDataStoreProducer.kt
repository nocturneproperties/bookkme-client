package library.cache.impl.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import library.files.api.FileProvider

internal class FilePreferenceDataStoreProducer(
    private val fileProvider: FileProvider,
) : PreferenceDataStoreProducer {
    override fun produce(name: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { fileProvider.touchPrefsFile("$name.preferences_pb") },
        )
    }
}
