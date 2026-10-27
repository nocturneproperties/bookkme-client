package library.cache.impl.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import library.cache.api.Cache
import me.bookk.core.Logger
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
import kotlin.time.Duration

internal class TtlCacheImpl(cacheName: String) : Cache {

    private val logger = Logger.create(cacheName)
    private val mutex = Mutex()
    private val cacheMap = mutableMapOf<String, CacheEntry<*>>()

    override suspend fun <T : Any> get(key: String, cls: KClass<T>): T? = mutex.withLock {
        val entry = cacheMap[key] ?: return null
        if (entry.expirationMark.hasPassedNow()) return null
        logger.d("Cache hit: K: $key")
        return cls.safeCast(entry.data)
    }

    override suspend fun <T : Any> set(key: String, ttl: Duration, value: T, cls: KClass<T>) {
        mutex.withLock {
            clearExpiredEntries()
            cacheMap[key] = CacheEntry(value, ttl)
            logger.d("Cache put: K: $key, V: $value")
        }
    }

    override suspend fun clear() {
        cacheMap.clear()
        logger.d("Cache cleared")
    }

    private fun clearExpiredEntries() {
        val toRemove = mutableSetOf<String>()
        cacheMap.forEach {
            if (it.value.expirationMark.hasPassedNow()) {
                toRemove.add(it.key)
                logger.d("Cache expired: K: ${it.key}")
            }
        }
        cacheMap.keys.removeAll(toRemove)
    }
}