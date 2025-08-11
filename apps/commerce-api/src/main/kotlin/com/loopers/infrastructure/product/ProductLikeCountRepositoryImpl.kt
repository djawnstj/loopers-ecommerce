package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeCount
import com.loopers.domain.product.ProductLikeCountRepository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Component

@Component
class ProductLikeCountRepositoryImpl(
    private val jpaProductLikeCountRepository: JpaProductLikeCountRepository,
) : ProductLikeCountRepository {
    override fun save(productLikeCount: ProductLikeCount): ProductLikeCount =
        jpaProductLikeCountRepository.save(productLikeCount)

    @Lock(LockModeType.OPTIMISTIC)
    override fun findByProductIdWithOptimisticLock(productId: Long): ProductLikeCount? =
        jpaProductLikeCountRepository.findAll {
            select(
                entity(ProductLikeCount::class),
            ).from(
                entity(ProductLikeCount::class),
            ).whereAnd(
                path(ProductLikeCount::productId).eq(productId),
                path(ProductLikeCount::deletedAt).isNull(),
            )
        }.firstOrNull()
}
