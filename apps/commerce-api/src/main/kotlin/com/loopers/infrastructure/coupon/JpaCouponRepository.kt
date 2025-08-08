package com.loopers.infrastructure.coupon

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.coupon.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCouponRepository : JpaRepository<Coupon, Long>, KotlinJdslJpqlExecutor
