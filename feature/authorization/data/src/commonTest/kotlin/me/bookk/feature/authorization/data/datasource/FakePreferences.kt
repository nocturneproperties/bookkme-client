package me.bookk.feature.authorization.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import library.cache.api.Preferences
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

internal class FakePreferences : Preferences {
    private val values = MutableStateFlow<Map<String, Any>>(emptyMap())

    override suspend fun <T : Any> get(key: Preferences.Key<T>, cls: KClass<T>): T? {
        return cls.safeCast(values.value[key.name])
    }

    override fun <T : Any> getFlow(key: Preferences.Key<T>, cls: KClass<T>): Flow<T?> {
        return values.map { cls.safeCast(it[key.name]) }
    }

    override suspend fun <T : Any> set(key: Preferences.Key<T>, value: T?, cls: KClass<T>) {
        values.value =
            if (value == null) values.value - key.name else values.value + (key.name to value)
    }

    override suspend fun <T : Any> remove(key: Preferences.Key<T>, cls: KClass<T>) {
        values.value = values.value - key.name
    }

    override suspend fun clear() {
        values.value = emptyMap()
    }
}
