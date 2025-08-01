package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.ProductItemRepository
import org.springframework.stereotype.Component

@Component
class ProductItemRepositoryImpl(
    private val jpaProductItemRepository: JpaProductItemRepository,
) : ProductItemRepository {
    override fun findAllByIds(productItemIds: List<Long>): List<ProductItem> = jpaProductItemRepository.findAll {
        select(
            entity(ProductItem::class),
        ).from(
            entity(ProductItem::class),
            fetchJoin(path(ProductItem::product)),
        ).whereAnd(
            path(ProductItem::id).`in`(productItemIds),
            path(ProductItem::deletedAt).isNull(),
        )
    }.filterNotNull()
}
