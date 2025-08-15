package com.loopers.domain.product

import com.loopers.domain.brand.Brand
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.product.params.GetProductParam
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
    fun getProductItemsDetailWithLock(productItemIds: List<Long>): ProductItems
    fun deductProductItemsQuantity(param: DeductProductItemsQuantityParam)
    fun increaseProductLikeCount(id: Long)
    fun decreaseProductLikeCount(id: Long)
}

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {
    override fun getProducts(param: GetProductParam): List<Product> =
        productRepository.findBySortType(param.brandId, param.sortType, param.page, param.perPage)

    override fun getActiveProductInfo(id: Long): Product =
        productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_ITEM_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

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
}
