package com.loopers.infrastructure.point

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.point.UserPoint
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserPointRepository : JpaRepository<UserPoint, Long>, KotlinJdslJpqlExecutor
