package com.loopers.application.product

import com.loopers.application.product.command.GetProductDetailResult
import com.loopers.application.product.command.GetProductRankingCommand
import com.loopers.application.product.command.GetProductRankingResult
import com.loopers.application.product.command.GetProductsCommand
import com.loopers.application.product.command.GetProductsResult
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductRankings
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.RankingPeriod
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

    fun getProductRankings(command: GetProductRankingCommand): GetProductRankingResult {
        val productRankings = when (command.period) {
            RankingPeriod.DAILY -> getDailyRankings(command)
            RankingPeriod.WEEKLY -> getWeeklyRankings(command)
            RankingPeriod.MONTHLY -> getMonthlyRankings(command)
        }

        return productRankings.toResult()
    }

    private fun getDailyRankings(command: GetProductRankingCommand): ProductRankings {
        val param = GetProductRankingParam(
            page = command.pageable.pageNumber,
            perPage = command.pageable.pageSize,
            date = command.date,
        )

        return productService.getProductRanking(param)
    }

    private fun getWeeklyRankings(command: GetProductRankingCommand): ProductRankings = productService.getWeeklyProductRanking(
        command.date,
        command.pageable.pageNumber,
        command.pageable.pageSize,
    )

    private fun getMonthlyRankings(command: GetProductRankingCommand): ProductRankings = productService.getMonthlyProductRanking(
        command.date,
        command.pageable.pageNumber,
        command.pageable.pageSize,
    )

    private fun ProductRankings.toResult(): GetProductRankingResult {
        val productIds = rankings.map(ProductRankings.ProductRanking::productId)
        val products = productService.findAllByIds(productIds)

        val rankedProducts = rankings.mapNotNull { ranking ->
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

        return GetProductRankingResult(rankedProducts, this)
    }
}
