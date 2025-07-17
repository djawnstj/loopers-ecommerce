package com.loopers.point.infrastructure

import com.loopers.point.domain.UserPoint
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserPointRepository : JpaRepository<UserPoint, Long> {
    fun findByUserId(userId: Long): UserPoint?
}
