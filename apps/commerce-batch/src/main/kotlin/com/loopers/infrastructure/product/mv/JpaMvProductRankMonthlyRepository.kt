package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankMonthly
import org.springframework.data.jpa.repository.JpaRepository

interface JpaMvProductRankMonthlyRepository : JpaRepository<MvProductRankMonthly, Long>