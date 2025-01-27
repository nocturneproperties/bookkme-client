package me.bookk.feature.platform.data

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
import me.bookk.feature.platform.domain.datasource.FileProvider
import kotlin.reflect.KClass
import me.bookk.feature.platform.domain.datasource.Preferences as BookkPreferences

class PreferencesImpl(
    fileName: String,
    fileProvider: FileProvider
) : BookkPreferences {

    private val dataStore = PreferenceDataStoreFactory.createWithPath(
        produceFile = { fileProvider.touchPrefsFile("$fileName.preferences_pb") }
    )

    override suspend fun <T : Any> get(key: BookkPreferences.Key<T>, cls: KClass<T>): T? {
        return dataStore.data.map { it[key.toAndroidX(cls)] }.first()
    }

    override fun <T : Any> getFlow(key: BookkPreferences.Key<T>, cls: KClass<T>): Flow<T?> {
        return dataStore.data.map { it[key.toAndroidX(cls)] }
    }

    override suspend fun <T : Any> set(key: BookkPreferences.Key<T>, value: T?, cls: KClass<T>) {
        dataStore.edit { prefs ->
            if (value == null) {
                prefs.remove(key.toAndroidX(cls))
            } else {
                prefs[key.toAndroidX(cls)] = value
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> BookkPreferences.Key<T>.toAndroidX(cls: KClass<T>): Preferences.Key<T> {
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