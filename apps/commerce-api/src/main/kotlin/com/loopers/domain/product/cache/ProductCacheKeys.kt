package com.loopers.domain.product.cache

import com.loopers.cache.CacheKey
import com.loopers.domain.product.params.GetProductParam
import java.time.Duration

sealed class ProductCacheKeys(override val ttl: Duration) : CacheKey {
    data class GetProducts(private val param: GetProductParam) : ProductCacheKeys(Duration.ofMinutes(1)) {
        override val key: String = "GET_PRODUCTS:${param.hashCode()}"
    }
}
