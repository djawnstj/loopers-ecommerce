package com.loopers.domain.product

import com.loopers.cache.CacheRepository
import com.loopers.cache.SortedCacheRepository
import com.loopers.cache.findAll
import com.loopers.domain.brand.Brand
import com.loopers.domain.product.cache.ProductCacheKey
import com.loopers.domain.product.cache.ProductCacheKeys
import com.loopers.domain.product.mv.MvProductRankMonthlyRepository
import com.loopers.domain.product.mv.MvProductRankWeeklyRepository
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.params.GetProductRankingParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId

interface ProductService {
    fun getProducts(param: GetProductParam): List<Product>
    fun getActiveProductInfo(id: Long): Product
    fun findAllByIds(ids: List<Long>): List<Product>
    fun getProductRanking(param: GetProductRankingParam): ProductRankings
    fun getWeeklyProductRanking(date: LocalDate, page: Int, size: Int): ProductRankings
    fun getMonthlyProductRanking(date: LocalDate, page: Int, size: Int): ProductRankings
    fun getProductRank(productId: Long, date: LocalDate): Long?
    fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView
    fun getProductItemsDetailWithLock(productItemIds: List<Long>): ProductItems
    fun deductProductItemsQuantity(param: DeductProductItemsQuantityParam)
    fun increaseProductLikeCount(id: Long)
    fun decreaseProductLikeCount(id: Long)
}

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val cacheRepository: CacheRepository,
    private val sortedCacheRepository: SortedCacheRepository,
    private val mvProductRankWeeklyRepository: MvProductRankWeeklyRepository,
    private val mvProductRankMonthlyRepository: MvProductRankMonthlyRepository,
) : ProductService {
    override fun getProducts(param: GetProductParam): List<Product> {
        val getProductsCacheKey = ProductCacheKeys.GetProducts(param)
        val cache: List<Product> = cacheRepository.findAll(getProductsCacheKey)

        if (cache.isNotEmpty()) {
            return cache
        }

        val results = productRepository.findBySortType(param.brandId, param.sortType, param.page, param.perPage)
        cacheRepository.save(getProductsCacheKey, results)

        return results
    }

    override fun getActiveProductInfo(id: Long): Product =
        productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_ITEM_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

    override fun findAllByIds(ids: List<Long>): List<Product> =
        productRepository.findAllByIds(ids)

    override fun getProductRanking(param: GetProductRankingParam): ProductRankings {
        val cacheKey = ProductCacheKey.ProductRankingPerDays(param.date)
        val startIndex = param.page * param.perPage
        val endIndex = startIndex + param.perPage - 1

        val rankings = sortedCacheRepository.findRangeByIndex(
            cacheKey,
            startIndex.toLong(),
            endIndex.toLong(),
            Long::class,
        )

        val totalCount = getTotalRankingCount(param.date)

        return ProductRankings(
            rankings = rankings.map { ProductRankings.ProductRanking(it.value, it.rank) },
            totalCount = totalCount,
            page = param.page,
            perPage = param.perPage,
        )
    }

    override fun getProductRank(productId: Long, date: LocalDate): Long? {
        val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
        return sortedCacheRepository.findRankByValue(cacheKey, productId)?.let {
            getTotalRankingCount(date) - it
        }
    }

    override fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView {
        return ProductDetailView(productDetail, brandDetail)
    }

    override fun getProductItemsDetailWithLock(productItemIds: List<Long>): ProductItems {
        val productItems = productItemIds.sorted()
            .map { productItemId ->
                productRepository.findProductItemByProductItemIdWithPessimisticWrite(productItemId)
                    ?: throw CoreException(ErrorType.PRODUCT_ITEM_NOT_FOUND, "일부 상품 아이템을 찾을 수 없습니다.")
            }

        if (productItems.size != productItemIds.size) {
            throw CoreException(ErrorType.PRODUCT_ITEM_NOT_FOUND, "일부 상품 아이템을 찾을 수 없습니다.")
        }

        return ProductItems(productItems.toMutableList())
    }

    @Transactional
    override fun deductProductItemsQuantity(param: DeductProductItemsQuantityParam) {
        param.items.sortedBy(DeductProductItemsQuantityParam.DeductItem::productItemId)
            .forEach { deductItem ->
                productRepository.findProductItemByProductItemIdWithPessimisticWrite(deductItem.productItemId)
                    ?.deduct(deductItem.quantity)
                    ?: throw CoreException(ErrorType.PRODUCT_ITEM_NOT_FOUND, "일부 상품 아이템을 찾을 수 없습니다.")
            }
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 1.5),
        recover = "recoverIncreaseProductLikeCount",
    )
    override fun increaseProductLikeCount(id: Long) {
        val product = productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

        product.increaseLikeCount()
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 1.5),
        recover = "recoverDecreaseProductLikeCount",
    )
    override fun decreaseProductLikeCount(id: Long) {
        val product = productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

        product.decreaseLikeCount()
    }

    @Recover
    fun recoverIncreaseProductLikeCount(ex: Exception, id: Long) {
        // 실패 처리(ex, id)
        throw CoreException(ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT, "식별자 $id 에 해당하는 상품 좋아요 수 증가에 실패했습니다.")
    }

    @Recover
    fun recoverDecreaseProductLikeCount(ex: Exception, id: Long) {
        // 실패 처리(ex, id)
        throw CoreException(ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT, "식별자 $id 에 해당하는 상품 좋아요 수 감소에 실패했습니다.")
    }

    override fun getWeeklyProductRanking(date: LocalDate, page: Int, size: Int): ProductRankings {
        val startDateTime = date.atStartOfDay(ZoneId.systemDefault())
        val endDateTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault())

        val weeklyRanks = mvProductRankWeeklyRepository.findByCreatedAtBetween(
            startDateTime, endDateTime, org.springframework.data.domain.PageRequest.of(page, size),
        )

        val rankings = weeklyRanks.content.map {
            ProductRankings.ProductRanking(it.productId, it.rank)
        }

        return ProductRankings(
            rankings = rankings,
            totalCount = weeklyRanks.totalElements,
            page = page,
            perPage = size,
        )
    }

    override fun getMonthlyProductRanking(date: LocalDate, page: Int, size: Int): ProductRankings {
        val startDateTime = date.atStartOfDay(ZoneId.systemDefault())
        val endDateTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault())

        val monthlyRanks = mvProductRankMonthlyRepository.findByCreatedAtBetween(
            startDateTime, endDateTime, org.springframework.data.domain.PageRequest.of(page, size),
        )

        val rankings = monthlyRanks.content.map {
            ProductRankings.ProductRanking(it.productId, it.rank)
        }

        return ProductRankings(
            rankings = rankings,
            totalCount = monthlyRanks.totalElements,
            page = page,
            perPage = size,
        )
    }

    private fun getTotalRankingCount(date: LocalDate): Long {
        val cacheKey = ProductCacheKey.ProductRankingPerDays(date)
        return sortedCacheRepository.findRangeByIndex(cacheKey, 0, -1, Long::class).size.toLong()
    }
}
