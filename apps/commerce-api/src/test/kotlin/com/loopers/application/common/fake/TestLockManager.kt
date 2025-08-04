package com.loopers.application.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SimpleLockManagerTest {

    @Nested
    inner class `락을 획득할 때` {

        @Test
        fun `단일 키로 락을 획득할 수 있다`() {
            // given
            val cut = SimpleLockManager()
            val key = "test-key"

            // when
            val result = cut.tryLock(key)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `여러 키로 락을 획득할 수 있다`() {
            // given
            val cut = SimpleLockManager()

            // when
            val result = cut.tryLock("user", "1", "product", "2")

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `락을 획득 하면 true 를 반환 한다`() {
            // given
            val cut = SimpleLockManager()
            val key = "test-key"

            // when
            val result = cut.tryLock(key)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `이미 락이 있는 키에 대해 락 획득을 시도하면 false 를 반환 한다`() {
            // given
            val cut = SimpleLockManager()
            val key = "test-key"
            cut.tryLock(key)

            // when
            val result = cut.tryLock(key)

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `다른 키에 대해서는 독립적으로 락을 획득할 수 있다`() {
            // given
            val cut = SimpleLockManager()
            val key1 = "key1"
            val key2 = "key2"
            cut.tryLock(key1)

            // when
            val result = cut.tryLock(key2)

            // then
            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class `락을 해제할 때` {

        @Test
        fun `락을 해제하면 다시 락을 획득할 수 있다`() {
            // given
            val cut = SimpleLockManager()
            val key = "test-key"
            cut.tryLock(key)

            // when
            cut.unlock(key)

            // then
            val result = cut.tryLock(key)
            assertThat(result).isTrue()
        }

        @Test
        fun `여러 키로 구성된 락을 해제할 수 있다`() {
            // given
            val cut = SimpleLockManager()
            cut.tryLock("user", "1", "product", "2")

            // when
            cut.unlock("user", "1", "product", "2")

            // then
            val result = cut.tryLock("user", "1", "product", "2")
            assertThat(result).isTrue()
        }

        @Test
        fun `존재하지 않는 키를 해제해도 오류가 발생하지 않는다`() {
            // given
            val cut = SimpleLockManager()

            // when then
            cut.unlock("non-existent-key")
        }
    }
}
