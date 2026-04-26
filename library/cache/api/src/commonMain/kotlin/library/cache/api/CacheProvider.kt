package library.cache.api

import kotlin.reflect.KClass
import kotlin.time.Duration

interface CacheProvider {
    fun get(cacheName: String): Cache
}

interface Cache {
    suspend fun <T : Any> get(key: String, cls: KClass<T>): T?

    suspend fun <T : Any> set(key: String, ttl: Duration, value: T, cls: KClass<T>)

    suspend fun clear()
}

suspend inline fun <reified T : Any> Cache.get(key: String): T? {
    return get(key, T::class)
}

suspend inline fun <reified T : Any> Cache.set(key: String, value: T, ttl: Duration = Duration.INFINITE) {
    return set(key, ttl, value, T::class)
}
