package com.loopers.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.loopers.utils.RedisCleanUp
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisConnectionFactory

@SpringBootApplication
@Import(CacheTestConfig::class)
class CacheTestApplication

class CacheTestConfig {

    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()

    @Bean
    fun redisCleanUp(redisConnectionFactory: RedisConnectionFactory): RedisCleanUp {
        return RedisCleanUp(redisConnectionFactory)
    }
}
