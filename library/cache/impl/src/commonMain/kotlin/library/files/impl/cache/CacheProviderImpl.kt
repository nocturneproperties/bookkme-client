package library.files.impl.cache

import library.cache.api.Cache
import library.cache.api.CacheProvider

internal class CacheProviderImpl : CacheProvider {
    override fun get(cacheName: String): Cache {
        return TtlCacheImpl(cacheName)
    }
}