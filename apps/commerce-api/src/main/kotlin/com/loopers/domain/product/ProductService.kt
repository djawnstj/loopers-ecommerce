package com.loopers.domain.product

import com.loopers.domain.brand.Brand
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ProductService {
    fun getProducts(param: GetProductParam): List<Product>
    fun getActiveProductInfo(id: Long): Product
    fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView
}

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
) : ProductService {
    override fun getProducts(param: GetProductParam): List<Product> =
        productRepository.findBySortType(param.brandId, param.sortType, param.page, param.perPage)

    override fun getActiveProductInfo(id: Long): Product =
        productRepository.findActiveProductById(id) ?: throw CoreException(
            ErrorType.PRODUCT_NOT_FOUND,
            "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.",
        )

    override fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView {
        val productLikeCount = productLikeCountRepository.findByProductId(productDetail.id)?.count ?: LikeCount.ZERO

        return ProductDetailView(productDetail, brandDetail, productLikeCount)
    }
}
