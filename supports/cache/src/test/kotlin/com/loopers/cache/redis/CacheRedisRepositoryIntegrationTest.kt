package com.loopers.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.cache.CacheKey
import com.loopers.cache.CacheTestApplication
import com.loopers.cache.find
import com.loopers.cache.findAll
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.TestConstructor
import java.time.Duration

@SpringBootTest(classes = [CacheTestApplication::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CacheRedisRepositoryIntegrationTest(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisCleanUp: RedisCleanUp,
    objectMapper: ObjectMapper,
) {
    private val cacheRedisRepository = CacheRedisRepository(redisTemplate, objectMapper)

    @AfterEach
    fun cleanUp() {
        redisCleanUp.truncateAll()
    }

    @Nested
    inner class `캐시 저장을 할 때` {
        @Test
        fun `객체를 JSON 문자열로 저장한다`() {
            // given
            val key = TestCacheKey("test:key", Duration.ofMinutes(10))
            val value = TestData("홍길동", 25)

            // when
            cacheRedisRepository.save(key, value)

            // then
            val storedValue = redisTemplate.opsForValue().get(key.key)
            assertThat(storedValue).isEqualTo("""{"name":"홍길동","age":25}""")
        }

        @Test
        fun `TTL을 설정하여 저장한다`() {
            // given
            val key = TestCacheKey("test:ttl", Duration.ofSeconds(5))
            val value = TestData("김철수", 30)

            // when
            cacheRedisRepository.save(key, value)

            // then
            val ttl = redisTemplate.getExpire(key.key)
            assertThat(ttl).isGreaterThan(0).isLessThanOrEqualTo(5)
        }

        @Test
        fun `리스트 객체를 저장한다`() {
            // given
            val key = TestCacheKey("test:list", Duration.ofMinutes(5))
            val values = listOf(
                TestData("홍길동", 25),
                TestData("김철수", 30),
            )

            // when
            cacheRedisRepository.save(key, values)

            // then
            val storedValue = redisTemplate.opsForValue().get(key.key)
            assertThat(storedValue).isEqualTo("""[{"name":"홍길동","age":25},{"name":"김철수","age":30}]""")
        }
    }

    @Nested
    inner class `단일 객체 조회를 할 때` {
        @Test
        fun `저장된 객체를 조회한다`() {
            // given
            val key = TestCacheKey("test:find", Duration.ofMinutes(10))
            val originalData = TestData("홍길동", 25)
            cacheRedisRepository.save(key, originalData)

            // when
            val actual: TestData? = cacheRedisRepository.find(key)

            // then
            assertThat(actual).isEqualTo(originalData)
        }

        @Test
        fun `존재하지 않는 키로 조회하면 null을 반환한다`() {
            // given
            val key = TestCacheKey("test:notfound", Duration.ofMinutes(10))

            // when
            val actual: TestData? = cacheRedisRepository.find(key)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `만료된 키로 조회하면 null을 반환한다`() {
            // given
            val key = TestCacheKey("test:expired", Duration.ofMillis(1))
            val data = TestData("만료된데이터", 99)
            cacheRedisRepository.save(key, data)

            // when
            Thread.sleep(10)
            val actual: TestData? = cacheRedisRepository.find(key)

            // then
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class `리스트 객체 조회를 할 때` {
        @Test
        fun `저장된 리스트를 조회한다`() {
            // given
            val key = TestCacheKey("test:findAll", Duration.ofMinutes(10))
            val originalData = listOf(
                TestData("홍길동", 25),
                TestData("김철수", 30),
                TestData("이영희", 28),
            )
            cacheRedisRepository.save(key, originalData)

            // when
            val actual: List<TestData> = cacheRedisRepository.findAll(key)

            // then
            assertThat(actual).hasSize(3)
                .containsExactly(
                    TestData("홍길동", 25),
                    TestData("김철수", 30),
                    TestData("이영희", 28),
                )
        }

        @Test
        fun `존재하지 않는 키로 리스트 조회하면 빈 리스트를 반환한다`() {
            // given
            val key = TestCacheKey("test:emptyList", Duration.ofMinutes(10))

            // when
            val actual: List<TestData> = cacheRedisRepository.findAll(key)

            // then
            assertThat(actual).isEmpty()
        }

        @Test
        fun `빈 리스트를 저장하고 조회한다`() {
            // given
            val key = TestCacheKey("test:emptyListSave", Duration.ofMinutes(10))
            val emptyList = emptyList<TestData>()
            cacheRedisRepository.save(key, emptyList)

            // when
            val actual: List<TestData> = cacheRedisRepository.findAll(key)

            // then
            assertThat(actual).isEmpty()
        }
    }

    private data class TestCacheKey(
        override val key: String,
        override val ttl: Duration,
    ) : CacheKey

    private data class TestData(
        val name: String,
        val age: Int,
    )
}
