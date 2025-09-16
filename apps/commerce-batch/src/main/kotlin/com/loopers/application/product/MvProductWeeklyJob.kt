package com.loopers.application.product

import com.loopers.domain.product.ProductMetrics
import com.loopers.domain.product.ProductMetricsRepository
import com.loopers.domain.product.mv.MvProductRankWeekly
import com.loopers.domain.product.mv.MvProductRankWeeklyRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class MvProductWeeklyJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val productMetricsRepository: ProductMetricsRepository,
    private val mvProductRankWeeklyRepository: MvProductRankWeeklyRepository,
) {

    @Bean
    fun weeklyRankingJob(): Job {
        return JobBuilder("weeklyRankingJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(weeklyStep())
            .build()
    }

    @Bean
    fun weeklyStep(): Step {
        return StepBuilder("weeklyStep", jobRepository)
            .chunk<ProductMetrics, MvProductRankWeekly>(10, transactionManager)
            .reader(weeklyProductReader())
            .processor(weeklyProcessor())
            .writer(weeklyWriter())
            .build()
    }

    @Bean
    fun weeklyProductReader(): ListItemReader<ProductMetrics> {
        val today = LocalDate.now()
        val weekStart = today.minusDays(6)

        val productMetrics = productMetricsRepository.findByMetricDateBetweenOrderByRankAscLimit(weekStart, today, 100)

        return ListItemReader(productMetrics)
    }

    @Bean
    fun weeklyProcessor(): ItemProcessor<ProductMetrics, MvProductRankWeekly> = ItemProcessor { productMetrics ->
        MvProductRankWeekly(
            productId = productMetrics.productId,
            productName = productMetrics.productName,
            brandId = productMetrics.brandId,
            likeCount = productMetrics.likeCount,
            rank = productMetrics.rank,
        )
    }

    @Bean
    fun weeklyWriter(): ItemWriter<MvProductRankWeekly> {
        return ItemWriter { items ->
            mvProductRankWeeklyRepository.saveAll(items.toList())
        }
    }
}
