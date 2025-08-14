package com.loopers.cache

import kotlin.reflect.KClass

interface CacheRepository {
    fun <T> save(key: CacheKey, value: T)
    fun <T : Any> find(key: CacheKey, type: KClass<T>): T?
    fun <T : Any> findAll(key: CacheKey, type: KClass<T>): List<T>
}

inline fun <reified T : Any> CacheRepository.find(key: CacheKey): T? = find(key, T::class)
inline fun <reified T : Any> CacheRepository.findAll(key: CacheKey): List<T> = findAll(key, T::class)
