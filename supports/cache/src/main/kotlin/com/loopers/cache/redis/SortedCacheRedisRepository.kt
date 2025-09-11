package com.loopers.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.cache.CacheKey
import com.loopers.cache.SortedCache
import com.loopers.cache.SortedCacheRepository
import org.springframework.data.redis.core.RedisTemplate
import kotlin.reflect.KClass

internal class SortedCacheRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : SortedCacheRepository {
    private val opsForZSet get() = redisTemplate.opsForZSet()

    override fun <T> save(key: CacheKey, score: Double, value: T) {
        opsForZSet.add(key.key, objectMapper.writeValueAsString(value), score)
    }

    override fun <T> existsByValue(key: CacheKey, value: T): Boolean =
        (opsForZSet.score(key.key, objectMapper.writeValueAsString(value)) != null)

    override fun <T : Any> findRangeByIndex(
        key: CacheKey,
        startIndex: Long,
        endIndex: Long,
        type: KClass<T>,
    ): List<SortedCache<T>> =
        opsForZSet.rangeWithScores(key.key, startIndex, endIndex)?.mapIndexed { index, it ->
            SortedCache(
                value = objectMapper.readValue(it.value, type.java),
                score = it.score,
                rank = startIndex + index,
            )
        } ?: emptyList()

    override fun <T : Any> findRankByValue(key: CacheKey, value: T): Long? =
        opsForZSet.rank(key.key, objectMapper.writeValueAsString(value))

    override fun <T : Any> incrementScore(key: CacheKey, value: T, score: Double) {
        opsForZSet.incrementScore(key.key, objectMapper.writeValueAsString(value), score)
    }
}
