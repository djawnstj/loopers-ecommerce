package com.loopers.infrastructure.like

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.like.Like
import com.loopers.domain.like.vo.TargetType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface JpaLikeRepository : JpaRepository<Like, Long>, KotlinJdslJpqlExecutor {
    @Transactional
    fun deleteByUserIdAndTargetIdAndTarget(userId: Long, targetId: Long, target: TargetType)
    fun existsByUserIdAndTargetIdAndTarget(userId: Long, targetId: Long, target: TargetType): Boolean
}
