package com.loopers.cache.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.cache.CacheRepository
import com.loopers.cache.redis.CacheRedisRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class CacheConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    fun cacheRepository(redisTemplate: RedisTemplate<String, String>, objectMapper: ObjectMapper): CacheRepository =
        CacheRedisRepository(redisTemplate, objectMapper)
}
