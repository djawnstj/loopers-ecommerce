package com.loopers.application.product

import com.loopers.cache.SortedCacheRepository
import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderItem
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.cache.ProductCacheKey
import com.loopers.support.IntegrationTestSupport
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class ProductFacadeIntegrationTest(
    private val sortedCacheRepository: SortedCacheRepository,
    @MockkBean private val orderService: OrderService,
) : IntegrationTestSupport() {

    private lateinit var cut: ProductFacade

    @BeforeEach
    fun setUp() {
        cut = ProductFacade(sortedCacheRepository, orderService)
    }

    @Nested
    inner class `상품 조회에 따른 랭킹 업데이트 시` {
        @Test
        fun `기존 랭킹 캐시가 없다면 캐시에 0_1점이 추가된다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)

            // when
            cut.updateViewRanking(productId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(0.1)
        }

        @Test
        fun `기존 랭킹 캐시가 없다면 캐시에 0_1점이 누적된다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            val initScore = 5.0

            sortedCacheRepository.save(cacheKey, initScore, productId)

            // when
            cut.updateViewRanking(productId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(5.1)
        }
    }

    @Nested
    inner class `상품 좋아요에 따른 랭킹 업데이트 시` {
        @Test
        fun `기존 랭킹 캐시가 없다면 캐시에 0_2점이 추가된다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)

            // when
            cut.updateLikeRanking(productId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(0.2)
        }

        @Test
        fun `기존 랭킹 캐시가 있다면 캐시에 0_2점이 누적된다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            val initScore = 3.0

            sortedCacheRepository.save(cacheKey, initScore, productId)

            // when
            cut.updateLikeRanking(productId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(3.2)
        }
    }

    @Nested
    inner class `주문 완료에 따른 랭킹 업데이트 시` {
        @Test
        fun `각 상품에 대해 기존 랭킹 캐시가 없다면 캐시에 점수가 추가된다`() {
            // given
            val orderId = 100L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)

            val order: Order = mockk()
            val orderItem: OrderItem = mockk()

            every { orderService.findByIdWithItems(orderId) } returns order
            every { order.orderItems } returns mutableListOf(orderItem)

            every { orderItem.productItemId } returns 10L
            every { orderItem.productPrice } returns BigDecimal("1000")
            every { orderItem.quantity } returns 2

            // when
            cut.updateOrderRankingByOrderId(orderId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(1200.0)
        }

        @Test
        fun `각 상품에 대해 기존 랭킹 캐시가 있다면 캐시에 점수가 누적된다`() {
            // given
            val orderId = 100L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            val initScore = 500.0

            sortedCacheRepository.save(cacheKey, initScore, 10L)

            val order: Order = mockk()
            val orderItem: OrderItem = mockk()

            every { orderService.findByIdWithItems(orderId) } returns order
            every { order.orderItems } returns mutableListOf(orderItem)

            every { orderItem.productItemId } returns 10L
            every { orderItem.productPrice } returns BigDecimal("1000")
            every { orderItem.quantity } returns 1

            // when
            cut.updateOrderRankingByOrderId(orderId, date)

            // then
            val actual = sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class)
            assertThat(actual).hasSize(1)
                .extracting("score")
                .containsExactly(1100.0)
        }
    }
}
