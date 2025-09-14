package com.loopers.batch.job

import com.loopers.batch.dto.ProductRankingAggregation
import com.loopers.batch.processor.ProductMetricsAggregationProcessor
import com.loopers.batch.reader.ProductMetricsItemReader
import com.loopers.batch.writer.ProductRankingItemWriter
import com.loopers.domain.product.ProductMetrics
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class ProductRankingBatchJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val readerFactory: ProductMetricsItemReader,
    private val processorFactory: ProductMetricsAggregationProcessor,
    private val writerFactory: ProductRankingItemWriter,
) {

    @Bean
    fun productRankingWeeklyJob(): Job {
        return JobBuilder("productRankingWeeklyJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(productRankingWeeklyStep())
            .build()
    }

    @Bean
    fun productRankingMonthlyJob(): Job {
        return JobBuilder("productRankingMonthlyJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(productRankingMonthlyStep())
            .build()
    }

    @Bean
    fun productRankingWeeklyStep(): Step {
        return StepBuilder("productRankingWeeklyStep", jobRepository)
            .chunk<ProductMetrics, ProductRankingAggregation>(1000, transactionManager)
            .reader(weeklyReader())
            .processor(weeklyProcessor())
            .writer(weeklyWriter())
            .build()
    }

    @Bean
    fun productRankingMonthlyStep(): Step {
        return StepBuilder("productRankingMonthlyStep", jobRepository)
            .chunk<ProductMetrics, ProductRankingAggregation>(1000, transactionManager)
            .reader(monthlyReader())
            .processor(monthlyProcessor())
            .writer(monthlyWriter())
            .build()
    }

    @Bean
    fun weeklyReader(): ItemReader<ProductMetrics> {
        val endDate = YESTERDAY
        val startDate = endDate.weeklyStartDate()

        return readerFactory.createWeeklyReader(
            entityManagerFactory = entityManagerFactory,
            startDate = startDate,
            endDate = endDate,
        )
    }

    @Bean
    fun weeklyProcessor(): ItemProcessor<ProductMetrics, ProductRankingAggregation> {
        val endDate = YESTERDAY
        val startDate = endDate.weeklyStartDate()

        return processorFactory.createWeeklyProcessor(
            startDate = startDate,
            endDate = endDate,
        )
    }

    @Bean
    fun weeklyWriter(): ItemWriter<ProductRankingAggregation> {
        return writerFactory.createWeeklyWriter(entityManagerFactory)
    }

    @Bean
    fun monthlyReader(): ItemReader<ProductMetrics> {
        val endDate = YESTERDAY
        val startDate = endDate.monthlyStartDate()

        return readerFactory.createMonthlyReader(
            entityManagerFactory = entityManagerFactory,
            startDate = startDate,
            endDate = endDate,
        )
    }

    @Bean
    fun monthlyProcessor(): ItemProcessor<ProductMetrics, ProductRankingAggregation> {
        val endDate = YESTERDAY
        val startDate = endDate.monthlyStartDate()

        return processorFactory.createMonthlyProcessor(
            startDate = startDate,
            endDate = endDate,
        )
    }

    @Bean
    fun monthlyWriter(): ItemWriter<ProductRankingAggregation> {
        return writerFactory.createMonthlyWriter(entityManagerFactory)
    }

    private fun LocalDate.weeklyStartDate(): LocalDate = minusDays(6)
    private fun LocalDate.monthlyStartDate(): LocalDate = withDayOfMonth(1)

    companion object {
        private val YESTERDAY get() = LocalDate.now().minusDays(1)
    }
}
