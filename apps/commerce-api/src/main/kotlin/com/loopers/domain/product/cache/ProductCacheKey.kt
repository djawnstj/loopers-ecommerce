package com.loopers.domain.product.cache

import com.loopers.cache.CacheKey
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class ProductCacheKey(override val ttl: Duration) : CacheKey {
    data class ProductRankingPerDays(private val today: LocalDate = LocalDate.now()) : ProductCacheKey(Duration.ofDays(2)) {
        override val key: String = "PRODUCT_RANKING:ALL:${today.format(DATE_FORMAT)}"
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}
