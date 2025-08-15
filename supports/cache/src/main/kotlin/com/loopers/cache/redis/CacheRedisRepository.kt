package com.loopers.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.cache.CacheKey
import com.loopers.cache.CacheRepository
import org.springframework.data.redis.core.RedisTemplate
import kotlin.reflect.KClass

internal class CacheRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CacheRepository {
    private val opsForValue get() = redisTemplate.opsForValue()

    override fun <T> save(key: CacheKey, value: T) {
        opsForValue.set(key.key, objectMapper.writeValueAsString(value), key.ttl)
    }

    override fun <T : Any> find(key: CacheKey, type: KClass<T>): T? =
        opsForValue.get(key.key)?.let { objectMapper.readValue(it, type.java) }

    override fun <T : Any> findAll(key: CacheKey, type: KClass<T>): List<T> =
        opsForValue.get(key.key)?.let {
            val listType = objectMapper.typeFactory.constructCollectionType(List::class.java, type.java)
            objectMapper.readValue(it, listType)
        } ?: emptyList()
}
