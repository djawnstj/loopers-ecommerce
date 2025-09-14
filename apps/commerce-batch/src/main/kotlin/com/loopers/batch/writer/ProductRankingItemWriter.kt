package com.loopers.batch.writer

import com.loopers.batch.dto.ProductRankingAggregation
import com.loopers.domain.product.mv.MvProductRankMonthly
import com.loopers.domain.product.mv.MvProductRankWeekly
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.stereotype.Component

@Component
class ProductRankingItemWriter {

    fun createWeeklyWriter(entityManagerFactory: EntityManagerFactory): ItemWriter<ProductRankingAggregation> {
        return ProductRankingToMvWeeklyWriter(
            createJpaWriter(entityManagerFactory),
        )
    }

    fun createMonthlyWriter(entityManagerFactory: EntityManagerFactory): ItemWriter<ProductRankingAggregation> {
        return ProductRankingToMvMonthlyWriter(
            createJpaWriter(entityManagerFactory),
        )
    }

    private fun <T> createJpaWriter(entityManagerFactory: EntityManagerFactory): JpaItemWriter<T> {
        return JpaItemWriterBuilder<T>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}

class ProductRankingToMvWeeklyWriter(
    private val jpaWriter: JpaItemWriter<MvProductRankWeekly>,
) : ItemWriter<ProductRankingAggregation> {

    override fun write(items: Chunk<out ProductRankingAggregation>) {
        // 점수로 정렬하여 순위 부여 (상위 100개만)
        val rankedItems = items.items
            .sortedByDescending { it.score }
            .take(100)
            .mapIndexed { index, aggregation ->
                MvProductRankWeekly(
                    productId = aggregation.productId,
                    productName = aggregation.productName,
                    brandId = aggregation.brandId,
                    likeCount = aggregation.likeCount,
                    score = aggregation.score,
                    rank = (index + 1).toLong(),
                    weekStartDate = aggregation.periodStartDate,
                    weekEndDate = aggregation.periodEndDate,
                )
            }

        if (rankedItems.isNotEmpty()) {
            jpaWriter.write(Chunk(rankedItems))
        }
    }
}

class ProductRankingToMvMonthlyWriter(
    private val jpaWriter: JpaItemWriter<MvProductRankMonthly>,
) : ItemWriter<ProductRankingAggregation> {

    override fun write(items: Chunk<out ProductRankingAggregation>) {
        // 점수로 정렬하여 순위 부여 (상위 100개만)
        val rankedItems = items.items
            .sortedByDescending { it.score }
            .take(100)
            .mapIndexed { index, aggregation ->
                MvProductRankMonthly(
                    productId = aggregation.productId,
                    productName = aggregation.productName,
                    brandId = aggregation.brandId,
                    likeCount = aggregation.likeCount,
                    score = aggregation.score,
                    rank = (index + 1).toLong(),
                    monthStartDate = aggregation.periodStartDate,
                    monthEndDate = aggregation.periodEndDate,
                )
            }

        if (rankedItems.isNotEmpty()) {
            jpaWriter.write(Chunk(rankedItems))
        }
    }
}
