package library.cache.impl.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.WebLocalStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer

internal class WebPreferenceDataStoreProducer : PreferenceDataStoreProducer {
    override fun produce(name: String): DataStore<Preferences> {
        val dataKey = "$name.preferences_pb"
        val storage = DataChangeStorage(
            WebLocalStorage(PreferencesSerializer, dataKey),
            dataKey,
            localStorageChangedKeys(),
        )
        return PreferenceDataStoreFactory.create(storage = storage)
    }
}
