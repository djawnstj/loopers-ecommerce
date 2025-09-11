package com.loopers.application.product

import com.loopers.cache.CacheKey
import com.loopers.cache.SortedCacheRepository
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.cache.ProductCacheKey
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProductFacade(
    private val sortedCacheRepository: SortedCacheRepository,
    private val orderService: OrderService,
) {
    fun updateViewRanking(productId: Long, date: LocalDate) {
        val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
        val scoreToAdd = VIEW_WEIGHT * VIEW_SCORE
        updateRanking(cacheKey, productId, scoreToAdd)
    }

    fun updateLikeRanking(productId: Long, date: LocalDate) {
        val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
        val scoreToAdd = LIKE_WEIGHT * LIKE_SCORE
        updateRanking(cacheKey, productId, scoreToAdd)
    }

    fun updateOrderRankingByOrderId(orderId: Long, date: LocalDate) {
        val order = orderService.findByIdWithItems(orderId) ?: return
        val cacheKey = ProductCacheKey.ProductRankingPerDays(date)

        order.orderItems.forEach { orderItem ->
            val orderScore = orderItem.productPrice.toDouble() * orderItem.quantity
            val scoreToAdd = ORDER_WEIGHT * orderScore
            updateRanking(cacheKey, orderItem.productItemId, scoreToAdd)
        }
    }

    private fun updateRanking(cacheKey: CacheKey, productId: Long, scoreToAdd: Double) {
        if (sortedCacheRepository.existsByValue(cacheKey, productId)) {
            sortedCacheRepository.incrementScore(cacheKey, productId, scoreToAdd)

            return
        }

        sortedCacheRepository.save(cacheKey, scoreToAdd, productId)
    }

    companion object {
        private const val VIEW_WEIGHT = 0.1
        private const val VIEW_SCORE = 1.0

        private const val LIKE_WEIGHT = 0.2
        private const val LIKE_SCORE = 1.0

        private const val ORDER_WEIGHT = 0.6
    }
}
