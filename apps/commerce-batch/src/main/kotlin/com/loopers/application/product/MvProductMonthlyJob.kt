package com.loopers.application.product

import com.loopers.domain.product.ProductMetrics
import com.loopers.domain.product.ProductMetricsRepository
import com.loopers.domain.product.mv.MvProductRankMonthly
import com.loopers.domain.product.mv.MvProductRankMonthlyRepository
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
class MvProductMonthlyJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val productMetricsRepository: ProductMetricsRepository,
    private val mvProductRankMonthlyRepository: MvProductRankMonthlyRepository,
) {

    @Bean
    fun monthlyRankingJob(): Job {
        return JobBuilder("monthlyRankingJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(monthlyStep())
            .build()
    }

    @Bean
    fun monthlyStep(): Step {
        return StepBuilder("monthlyStep", jobRepository)
            .chunk<ProductMetrics, MvProductRankMonthly>(10, transactionManager)
            .reader(monthlyProductReader())
            .processor(monthlyProcessor())
            .writer(monthlyWriter())
            .build()
    }

    @Bean
    fun monthlyProductReader(): ListItemReader<ProductMetrics> {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)

        val productMetrics = productMetricsRepository.findByMetricDateBetweenOrderByRankAscLimit(monthStart, today, 100)

        return ListItemReader(productMetrics)
    }

    @Bean
    fun monthlyProcessor(): ItemProcessor<ProductMetrics, MvProductRankMonthly> = ItemProcessor { productMetrics ->
        MvProductRankMonthly(
            productId = productMetrics.productId,
            productName = productMetrics.productName,
            brandId = productMetrics.brandId,
            likeCount = productMetrics.likeCount,
            rank = productMetrics.rank,
        )
    }

    @Bean
    fun monthlyWriter(): ItemWriter<MvProductRankMonthly> {
        return ItemWriter { items ->
            mvProductRankMonthlyRepository.saveAll(items.toList())
        }
    }
}
