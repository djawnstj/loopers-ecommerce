package com.loopers.application.product

import com.loopers.cache.SortedCacheRepository
import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderItem
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.cache.ProductCacheKey
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import java.math.BigDecimal
import java.time.LocalDate

class ProductFacadeTest {
    private val sortedCacheRepository: SortedCacheRepository = mockk(relaxed = true)
    private val orderService: OrderService = mockk()
    private val cut = ProductFacade(sortedCacheRepository, orderService)

    @Nested
    inner class `조회 랭킹을 업데이트할 때` {
        @Test
        fun `캐시에 상품이 존재하면 점수를 증가시킨다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            
            every { sortedCacheRepository.existsByValue(cacheKey, productId) } returns true

            // when
            cut.updateViewRanking(productId, date)

            // then
            verify { sortedCacheRepository.incrementScore(cacheKey, productId, 0.1) }
        }

        @Test
        fun `캐시에 상품이 존재하지 않으면 새로 저장한다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            
            every { sortedCacheRepository.existsByValue(cacheKey, productId) } returns false

            // when
            cut.updateViewRanking(productId, date)

            // then
            verify { sortedCacheRepository.save(cacheKey, 0.1, productId) }
        }
    }

    @Nested
    inner class `좋아요 랭킹을 업데이트할 때` {
        @Test
        fun `캐시에 상품이 존재하면 점수를 증가시킨다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            
            every { sortedCacheRepository.existsByValue(cacheKey, productId) } returns true

            // when
            cut.updateLikeRanking(productId, date)

            // then
            verify { sortedCacheRepository.incrementScore(cacheKey, productId, 0.2) }
        }

        @Test
        fun `캐시에 상품이 존재하지 않으면 새로 저장한다`() {
            // given
            val productId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
            
            every { sortedCacheRepository.existsByValue(cacheKey, productId) } returns false

            // when
            cut.updateLikeRanking(productId, date)

            // then
            verify { sortedCacheRepository.save(cacheKey, 0.2, productId) }
        }
    }

    @Nested
    inner class `주문 랭킹을 업데이트할 때` {
        @Test
        fun `주문이 존재하지 않으면 아무것도 하지 않는다`() {
            // given
            val orderId = 1L
            val date = LocalDate.of(2023, 12, 25)
            
            every { orderService.findByIdWithItems(orderId) } returns null

            // when
            cut.updateOrderRankingByOrderId(orderId, date)

            // then
            verify(exactly = 0) { sortedCacheRepository.existsByValue(any(), any<Long>()) }
            verify(exactly = 0) { sortedCacheRepository.incrementScore(any(), any(), any()) }
            verify(exactly = 0) { sortedCacheRepository.save(any(), any(), any<Long>()) }
        }

        @Test
        fun `주문 아이템이 없으면 아무것도 하지 않는다`() {
            // given
            val orderId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val order: Order = mockk()

            every { orderService.findByIdWithItems(orderId) } returns order
            every { order.orderItems } returns mutableListOf()

            // when
            cut.updateOrderRankingByOrderId(orderId, date)

            // then
            verify(exactly = 0) { sortedCacheRepository.existsByValue(any(), any<Long>()) }
            verify(exactly = 0) { sortedCacheRepository.incrementScore(any(), any(), any()) }
            verify(exactly = 0) { sortedCacheRepository.save(any(), any(), any<Long>()) }
        }

        @Test
        fun `주문 아이템이 있으면 각 상품의 점수를 증가시킨다`() {
            // given
            val orderId = 1L
            val date = LocalDate.of(2023, 12, 25)
            val order: Order = mockk()
            val orderItem1: OrderItem = mockk()
            val orderItem2: OrderItem = mockk()
            val cacheKey = ProductCacheKey.ProductRankingPerDays(date)

            every { orderService.findByIdWithItems(orderId) } returns order
            every { order.orderItems } returns mutableListOf(orderItem1, orderItem2)
            
            every { orderItem1.productItemId } returns 10L
            every { orderItem1.productPrice } returns BigDecimal("1000")
            every { orderItem1.quantity } returns 2
            
            every { orderItem2.productItemId } returns 20L
            every { orderItem2.productPrice } returns BigDecimal("2000")
            every { orderItem2.quantity } returns 1

            every { sortedCacheRepository.existsByValue(cacheKey, 10L) } returns true
            every { sortedCacheRepository.existsByValue(cacheKey, 20L) } returns false

            // when
            cut.updateOrderRankingByOrderId(orderId, date)

            // then
            verify { sortedCacheRepository.incrementScore(cacheKey, 10L, 1200.0) } // 0.6 * (1000.0 * 2)
            verify { sortedCacheRepository.save(cacheKey, 1200.0, 20L) } // 0.6 * (2000.0 * 1)
        }
    }
}
