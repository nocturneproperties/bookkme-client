package library.files.impl.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import library.files.api.FileProvider
import kotlin.reflect.KClass
import library.cache.api.Preferences as AppPreferences

internal class PreferencesImpl(
    fileName: String,
    fileProvider: FileProvider
) : AppPreferences {

    private val dataStore = PreferenceDataStoreFactory.createWithPath(
        produceFile = { fileProvider.touchPrefsFile("$fileName.preferences_pb") }
    )

    override suspend fun <T : Any> get(key: AppPreferences.Key<T>, cls: KClass<T>): T? {
        return dataStore.data.map { it[key.toAndroidX(cls)] }.first()
    }

    override fun <T : Any> getFlow(key: AppPreferences.Key<T>, cls: KClass<T>): Flow<T?> {
        return dataStore.data.map { it[key.toAndroidX(cls)] }
    }

    override suspend fun <T : Any> set(key: AppPreferences.Key<T>, value: T?, cls: KClass<T>) {
        dataStore.edit { prefs ->
            if (value == null) {
                prefs.remove(key.toAndroidX(cls))
            } else {
                prefs[key.toAndroidX(cls)] = value
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> AppPreferences.Key<T>.toAndroidX(cls: KClass<T>): Preferences.Key<T> {
        return when (cls) {
            Int::class -> intPreferencesKey(name)
            Long::class -> longPreferencesKey(name)
            String::class -> stringPreferencesKey(name)
            Boolean::class -> booleanPreferencesKey(name)
            Float::class -> floatPreferencesKey(name)
            Double::class -> doublePreferencesKey(name)
            Set::class -> stringPreferencesKey(name)
            ByteArray::class -> byteArrayPreferencesKey(name)
            else -> throw UnsupportedOperationException()
        } as Preferences.Key<T>
    }
}