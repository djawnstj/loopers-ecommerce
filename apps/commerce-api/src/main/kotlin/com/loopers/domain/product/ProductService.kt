package com.loopers.domain.product

import com.loopers.domain.brand.Brand
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ProductService {
    fun getProducts(param: GetProductParam): List<Product>
    fun getActiveProductInfo(id: Long): Product
    fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView
    fun getProductItemsDetail(productItemIds: List<Long>): List<ProductItem>
    fun deductProductItemsQuantity(param: DeductProductItemsQuantityParam)
    fun increaseProductLikeCount(id: Long)
    fun decreaseProductLikeCount(id: Long)
}

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
    private val productItemRepository: ProductItemRepository,
) : ProductService {
    override fun getProducts(param: GetProductParam): List<Product> =
        productRepository.findBySortType(param.brandId, param.sortType, param.page, param.perPage)

    override fun getActiveProductInfo(id: Long): Product =
        productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_ITEM_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

    override fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView {
        val productLikeCount = productLikeCountRepository.findByProductId(productDetail.id)?.count ?: LikeCount.ZERO

        return ProductDetailView(productDetail, brandDetail, productLikeCount)
    }

    override fun getProductItemsDetail(productItemIds: List<Long>): List<ProductItem> {
        val productItems = productItemRepository.findAllByIds(productItemIds)

        if (productItems.size != productItemIds.size) {
            throw CoreException(ErrorType.PRODUCT_ITEM_NOT_FOUND, "일부 상품 아이템을 찾을 수 없습니다.")
        }

        return productItems
    }

    @Transactional
    override fun deductProductItemsQuantity(param: DeductProductItemsQuantityParam) {
        val productItemIds = param.items.map(DeductProductItemsQuantityParam.DeductItem::productItemId)
        val productItems = getProductItemsDetail(productItemIds)

        param.items.forEach { deductItem ->
            val productItem = productItems.first { it.id == deductItem.productItemId }

            productItem.deduct(deductItem.quantity)
        }
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 1.5),
        recover = "recoverIncreaseProductLikeCount"
    )
    override fun increaseProductLikeCount(id: Long) {
        val product = productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

        val productLikeCount = productLikeCountRepository.findByProductId(product.id)
            ?: productLikeCountRepository.save(ProductLikeCount(id, 0))

        productLikeCount.increase()
    }

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 1.5),
        recover = "recoverDecreaseProductLikeCount"
    )
    override fun decreaseProductLikeCount(id: Long) {
        val product = productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

        val productLikeCount = productLikeCountRepository.findByProductId(product.id)
            ?: productLikeCountRepository.save(ProductLikeCount(id, 0))

        productLikeCount.decrease()
    }

    @Recover
    fun recoverIncreaseProductLikeCount(ex: Exception, id: Long) {
        // 실패 처리(ex, id)
        throw CoreException(ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT, "식별자 $id 에 해당하는 상품 좋아요 수 증가에 실패했습니다.")
    }

    @Recover
    fun recoverDecreaseProductLikeCount(ex: Exception, id: Long) {
        // 실패 처리(ex, id)
        throw CoreException(ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT, "식별자 $id 에 해당하는 상품 좋아요 수 증가에 실패했습니다.")
    }
}
