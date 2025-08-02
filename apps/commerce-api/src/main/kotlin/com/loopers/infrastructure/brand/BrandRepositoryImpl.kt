package com.loopers.infrastructure.brand

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.vo.BrandStatusType
import org.springframework.stereotype.Component

@Component
class BrandRepositoryImpl(
    private val jpaBrandRepository: JpaBrandRepository,
) : BrandRepository {
    override fun findActiveBrandById(id: Long): Brand? = jpaBrandRepository.findAll {
        select(
            entity(Brand::class),
        ).from(
            entity(Brand::class),
        ).whereAnd(
            path(Brand::id).eq(id),
            path(Brand::status).eq(BrandStatusType.ACTIVE),
            path(Brand::deletedAt).isNull(),
        )
    }.firstOrNull()
}
