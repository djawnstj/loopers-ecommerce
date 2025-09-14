package com.loopers.application.product

import com.loopers.application.product.command.GetProductDetailResult
import com.loopers.application.product.command.GetProductRankingCommand
import com.loopers.application.product.command.GetProductRankingResult
import com.loopers.application.product.command.GetProductsCommand
import com.loopers.application.product.command.GetProductsResult
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductRankings
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.params.GetProductRankingParam
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
) {
    fun getProducts(command: GetProductsCommand): GetProductsResult =
        GetProductsResult(productService.getProducts(command.toParam()))

    fun getProductDetail(productId: Long): GetProductDetailResult {
        val productInfo = productService.getActiveProductInfo(productId)
        val brandInfo = brandService.getActiveBrandDetail(productInfo.brandId)
        val productRank = productService.getProductRank(productId, LocalDate.now())

        return GetProductDetailResult(
            productService.aggregateProductDetail(productInfo, brandInfo),
            productRank,
        )
    }

    fun getProductRanking(command: GetProductRankingCommand): GetProductRankingResult {
        val param = GetProductRankingParam(
            page = command.pageable.pageNumber,
            perPage = command.pageable.pageSize,
            date = command.date,
        )
        val rankings = productService.getProductRanking(param)

        val productIds = rankings.rankings.map(ProductRankings.ProductRanking::productId)
        val products = productService.findAllByIds(productIds)

        val rankedProducts = rankings.rankings.mapNotNull { ranking ->
            val product = products.find { it.id == ranking.productId }
            product?.let {
                GetProductRankingResult.RankedProduct(
                    productId = it.id,
                    productName = it.name,
                    brandId = it.brandId,
                    likeCount = it.likeCount.value,
                    rank = ranking.rank + 1,
                )
            }
        }

        return GetProductRankingResult(rankedProducts, rankings)
    }
}
