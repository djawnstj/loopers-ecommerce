package com.loopers.batch.processor

import com.loopers.batch.dto.ProductRankingAggregation
import com.loopers.domain.product.ProductMetrics
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductMetricsAggregationProcessor(
    private val productInfoService: ProductInfoService
) {
    
    fun createWeeklyProcessor(
        startDate: LocalDate,
        endDate: LocalDate
    ): CompositeItemProcessor<ProductMetrics, ProductRankingAggregation> {
        
        val aggregationProcessor = ProductMetricsToAggregationProcessor(
            productInfoService = productInfoService,
            periodStartDate = startDate,
            periodEndDate = endDate
        )
        
        val compositeProcessor = CompositeItemProcessor<ProductMetrics, ProductRankingAggregation>()
        compositeProcessor.setDelegates(listOf(aggregationProcessor))
        
        return compositeProcessor
    }
    
    fun createMonthlyProcessor(
        startDate: LocalDate,
        endDate: LocalDate
    ): CompositeItemProcessor<ProductMetrics, ProductRankingAggregation> {
        
        val aggregationProcessor = ProductMetricsToAggregationProcessor(
            productInfoService = productInfoService,
            periodStartDate = startDate,
            periodEndDate = endDate
        )
        
        val compositeProcessor = CompositeItemProcessor<ProductMetrics, ProductRankingAggregation>()
        compositeProcessor.setDelegates(listOf(aggregationProcessor))
        
        return compositeProcessor
    }
}

class ProductMetricsToAggregationProcessor(
    private val productInfoService: ProductInfoService,
    private val periodStartDate: LocalDate,
    private val periodEndDate: LocalDate
) : ItemProcessor<ProductMetrics, ProductRankingAggregation> {
    
    private val productCache = mutableMapOf<Long, ProductInfo>()
    private val metricsCache = mutableMapOf<Long, MutableList<ProductMetrics>>()
    
    override fun process(item: ProductMetrics): ProductRankingAggregation? {
        // 메트릭을 캐시에 저장
        metricsCache.computeIfAbsent(item.productId) { mutableListOf() }.add(item)
        
        // 상품 정보 캐시
        if (!productCache.containsKey(item.productId)) {
            val productInfo = productInfoService.getProductInfo(item.productId)
            if (productInfo != null) {
                productCache[item.productId] = productInfo
            } else {
                return null // 상품 정보가 없으면 무시
            }
        }
        
        val productInfo = productCache[item.productId]!!
        val metrics = metricsCache[item.productId]!!
        
        // 집계 계산
        val totalLikeCount = metrics.sumOf { it.likeCount.toLong() }
        val totalViewCount = metrics.sumOf { it.viewCount.toLong() }
        val totalSalesCount = metrics.sumOf { it.salesCount.toLong() }
        
        val aggregation = ProductRankingAggregation(
            productId = item.productId,
            productName = productInfo.name,
            brandId = productInfo.brandId,
            likeCount = totalLikeCount,
            viewCount = totalViewCount,
            salesCount = totalSalesCount,
            score = 0.0, // 임시값
            periodStartDate = periodStartDate,
            periodEndDate = periodEndDate
        )
        
        // 점수 계산
        return aggregation.copy(score = aggregation.calculateScore())
    }
}

interface ProductInfoService {
    fun getProductInfo(productId: Long): ProductInfo?
}

data class ProductInfo(
    val id: Long,
    val name: String,
    val brandId: Long
)