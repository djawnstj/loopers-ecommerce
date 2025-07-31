package com.loopers.domain.brand

interface BrandRepository {
    fun findActiveBrandById(id: Long): Brand?
}
