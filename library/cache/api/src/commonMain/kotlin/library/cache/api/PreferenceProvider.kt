package library.cache.api

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface PreferenceProvider {
    fun get(fileName: String): Preferences
}

interface Preferences {

    suspend fun <T : Any> get(key: Key<T>, cls: KClass<T>): T?
    fun <T : Any> getFlow(key: Key<T>, cls: KClass<T>): Flow<T?>

    suspend fun <T : Any> set(key: Key<T>, value: T?, cls: KClass<T>)
    suspend fun <T : Any> remove(key: Key<T>, cls: KClass<T>)
    suspend fun clear()

    class Key<T>(val name: String)
}

suspend inline fun <reified T : Any> Preferences.get(key: Preferences.Key<T>): T? {
    return get(key, T::class)
}

inline fun <reified T : Any> Preferences.getFlow(key: Preferences.Key<T>): Flow<T?> {
    return getFlow(key, T::class)
}

suspend inline fun <reified T : Any> Preferences.set(key: Preferences.Key<T>, value: T?) {
    return set(key, value, T::class)
}

suspend inline fun <reified T : Any> Preferences.remove(key: Preferences.Key<T>) {
    return remove(key, T::class)
}