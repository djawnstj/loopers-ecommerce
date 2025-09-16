package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankWeekly
import org.springframework.data.jpa.repository.JpaRepository

interface JpaMvProductRankWeeklyRepository : JpaRepository<MvProductRankWeekly, Long>