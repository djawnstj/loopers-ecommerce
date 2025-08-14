package com.loopers.infrastructure.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.ProductItems
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.support.enums.sort.ProductSortType
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductRepositoryImpl(
    private val jpaProductRepository: JpaProductRepository,
    private val jpaProductItemRepository: JpaProductItemRepository,
    private val entityManager: EntityManager,
) : ProductRepository {
    override fun findBySortType(brandId: Long?, sortBy: ProductSortType?, offset: Int, limit: Int): List<Product> =
        jpaProductRepository.findAll(offset = offset, limit = limit) {
            select(
                entity(Product::class),
            ).from(
                entity(Product::class),
            ).whereAnd(
                eqBrandId(brandId),
                path(Product::status).eq(ProductStatusType.ACTIVE),
                path(Product::deletedAt).isNull(),
            ).orderBy(
                productSort(sortBy),
            )
        }.filterNotNull()

    override fun findActiveProductById(id: Long): Product? = jpaProductRepository.findAll {
        select(
            entity(Product::class),
        ).from(
            entity(Product::class),
            leftFetchJoin(path(Product::items).path(ProductItems::items)),
        ).whereAnd(
            path(Product::id).eq(id),
            path(Product::status).eq(ProductStatusType.ACTIVE),
            path(Product::deletedAt).isNull(),
        )
    }.firstOrNull()

    override fun findProductItemsByIds(productItemIds: List<Long>): List<ProductItem> = jpaProductItemRepository.findAll {
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

    @Transactional
    override fun findProductItemByProductItemIdWithPessimisticWrite(productItemId: Long): ProductItem? {
        val query = jpql {
            select(
                entity(ProductItem::class),
            ).from(
                entity(ProductItem::class),
            ).whereAnd(
                path(ProductItem::id).eq(productItemId),
                path(ProductItem::deletedAt).isNull(),
            )
        }

        return entityManager.createQuery(query, JpqlRenderContext())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .resultList.firstOrNull()
    }

    private fun Jpql.eqBrandId(brandId: Long?): Predicatable? = brandId?.let { path(Product::brandId).eq(it) }

    private fun Jpql.productSort(sortBy: ProductSortType?): SortNullsStep =
        when (sortBy) {
            ProductSortType.LATEST -> path(Product::saleStartAt).desc()
            else -> path(Product::id).desc()
        }
}
