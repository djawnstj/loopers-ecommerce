package com.loopers.application.like

import com.loopers.application.like.command.CreateProductLikeCommand
import com.loopers.application.like.command.DeleteProductLikeCommand
import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductLikeCountFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.like.JpaLikeRepository
import com.loopers.infrastructure.product.JpaProductLikeCountRepository
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.infrastructure.user.JpaUserRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LikeFacadeIntegrationTest(
    private val cut: LikeFacade,
    private val jpaProductRepository: JpaProductRepository,
    private val jpaLikeRepository: JpaLikeRepository,
    private val jpaUserRepository: JpaUserRepository,
    private val jpaProductLikeCountRepository: JpaProductLikeCountRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `좋아요를 생성할 때` {

        @Test
        fun `사용자가 존재하고 중복이 아니면 좋아요를 저장한다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "targetId", "target")
                .containsExactly(tuple(user.id, 1L, TargetType.PRODUCT))
        }

        @Test
        fun `동일한 좋아요가 이미 존재하면 중복 저장하지 않는다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command)
            cut.createProductLike(command)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `다른 사용자의 같은 타겟에 대한 좋아요는 독립적으로 저장된다`() {
            // given
            val user1 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 1`.toEntity())
            val user2 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 2`.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command1 = CreateProductLikeCommand(
                loginId = user1.loginId.value,
                targetId = product.id,
                target = TargetType.PRODUCT,
            )
            val command2 = CreateProductLikeCommand(
                loginId = user2.loginId.value,
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command1)
            cut.createProductLike(command2)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(2)
                .extracting("userId", "targetId", "target")
                .containsExactlyInAnyOrder(
                    tuple(user1.id, 1L, TargetType.PRODUCT),
                    tuple(user2.id, 1L, TargetType.PRODUCT),
                )
        }

        @Test
        fun `같은 사용자의 다른 타겟에 대한 좋아요는 독립적으로 저장된다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product1 = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            val product2 = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command1 = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product1.id,
                target = TargetType.PRODUCT,
            )
            val command2 = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product2.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command1)
            cut.createProductLike(command2)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(2)
                .extracting("userId", "targetId", "target")
                .containsExactlyInAnyOrder(
                    tuple(user.id, 1L, TargetType.PRODUCT),
                    tuple(user.id, 2L, TargetType.PRODUCT),
                )
        }

        @Test
        fun `동시에 같은 키로 요청해도 락으로 인해 중복 저장되지 않는다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            val threadCount = 5
            val executor = Executors.newFixedThreadPool(threadCount)
            val startLatch = CountDownLatch(1)
            val endLatch = CountDownLatch(threadCount)

            // when
            repeat(threadCount) {
                executor.submit {
                    try {
                        startLatch.await()
                        cut.createProductLike(command)
                    } finally {
                        endLatch.countDown()
                    }
                }
            }

            startLatch.countDown()
            endLatch.await(10, TimeUnit.SECONDS)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(1)

            executor.shutdown()
        }

        @Test
        fun `좋아요를 생성 하면 상품 좋아요 수를 증가한다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = CreateProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
            )

            // when
            cut.createProductLike(command)

            // then
            val actual = jpaProductLikeCountRepository.findByIdOrNull(1L)
            assertThat(actual)
                .extracting("productId", "count")
                .containsExactly(product.id, 1L)
        }

        @Test
        fun `두명이 동시에 같은 상품에 좋아요를 생성하면 좋아요 개수가 2개로 정상 반영된다`() {
            // given
            val user1 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 1`.toEntity())
            val user2 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 2`.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val threadCount = 2
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            val command1 = CreateProductLikeCommand(user1.loginId.value, product.id)
            val command2 = CreateProductLikeCommand(user2.loginId.value, product.id)

            // when
            listOf(command1, command2).forEach { command ->
                try {
                    cut.createProductLike(command)
                } finally {
                    latch.countDown()
                }
            }

            latch.await(10, TimeUnit.SECONDS)
            executor.shutdown()

            // then
            val actual = jpaProductLikeCountRepository.findByIdOrNull(1L)
            assertThat(actual?.count?.value).isEqualTo(2L)
        }
    }

    @Nested
    inner class `좋아요를 삭제할 때` {

        @Test
        fun `사용자가 존재하고 중복이 아니면 좋아요를 삭제한다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = DeleteProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
            )

            // when
            cut.deleteProductLike(command)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).isEmpty()
        }

        @Test
        fun `좋아요를 삭제 하면 상품 좋아요 수를 차감한다`() {
            // given
            val user = jpaUserRepository.saveAndFlush(UserFixture.기본.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())

            val command = DeleteProductLikeCommand(
                loginId = user.loginId.value,
                targetId = product.id,
            )

            // when
            cut.deleteProductLike(command)

            // then
            val actual = jpaProductLikeCountRepository.findByIdOrNull(1L)
            assertThat(actual)
                .extracting("productId", "count")
                .containsExactly(product.id, 0L)
        }

        @Test
        fun `두명이 동시에 같은 상품에 좋아요를 생성하면 좋아요 개수가 2개로 정상 반영된다`() {
            // given
            val user1 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 1`.toEntity())
            val user2 = jpaUserRepository.saveAndFlush(UserFixture.`로그인 ID 2`.toEntity())
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            jpaProductLikeCountRepository.saveAndFlush(ProductLikeCountFixture.`좋아요 10개`.toEntity(product.id))

            val threadCount = 2
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            val command1 = DeleteProductLikeCommand(user1.loginId.value, product.id)
            val command2 = DeleteProductLikeCommand(user2.loginId.value, product.id)

            // when
            listOf(command1, command2).forEach { command ->
                try {
                    cut.deleteProductLike(command)
                } finally {
                    latch.countDown()
                }
            }

            latch.await(10, TimeUnit.SECONDS)
            executor.shutdown()

            // then
            val actual = jpaProductLikeCountRepository.findByIdOrNull(1L)
            assertThat(actual?.count?.value).isEqualTo(8L)
        }
    }
}
