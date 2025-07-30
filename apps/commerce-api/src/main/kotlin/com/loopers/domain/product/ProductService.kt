package com.loopers.domain.product

import com.loopers.domain.product.params.GetProductParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service

interface ProductService {
    fun getProducts(param: GetProductParam): List<Product>

    fun getActiveProductDetail(id: Long): Product
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {
    override fun getProducts(param: GetProductParam): List<Product> =
        productRepository.findBySortType(param.brandId, param.sortType, param.page, param.perPage)

    override fun getActiveProductDetail(id: Long): Product =
        productRepository.findActiveProductById(id) ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "상품 식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.")
}
