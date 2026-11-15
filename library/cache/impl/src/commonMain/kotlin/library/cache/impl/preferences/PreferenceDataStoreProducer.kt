package library.cache.impl.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal fun interface PreferenceDataStoreProducer {
    fun produce(name: String): DataStore<Preferences>
}
