package com.loopers.support.fake

import com.loopers.cache.CacheKey
import com.loopers.cache.CacheRepository
import kotlin.reflect.KClass

class TestCacheRepository : CacheRepository {
    private val cache = mutableMapOf<String, Any>()

    override fun <T> save(key: CacheKey, value: T) {
        cache[key.key] = value as Any
    }

    override fun <T : Any> find(key: CacheKey, type: KClass<T>): T? {
        return cache[key.key] as? T
    }

    override fun <T : Any> findAll(key: CacheKey, type: KClass<T>): List<T> {
        return cache[key.key] as? List<T> ?: emptyList()
    }
}
