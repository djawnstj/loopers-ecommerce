package com.loopers.cache

import kotlin.reflect.KClass

interface SortedCacheRepository {
    fun <T> save(key: CacheKey, score: Double, value: T)
    fun <T> existsByValue(key: CacheKey, value: T): Boolean
    fun <T : Any> findRangeByIndex(key: CacheKey, startIndex: Long, endIndex: Long, type: KClass<T>): List<SortedCache<T>>
    fun <T : Any> findRankByValue(key: CacheKey, value: T): Long?
    fun <T : Any> incrementScore(key: CacheKey, value: T, score: Double)
}

data class SortedCache<T>(val value: T, val score: Double, val rank: Long)
