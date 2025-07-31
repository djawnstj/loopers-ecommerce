package com.loopers.infrastructure.like

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.like.Like
import org.springframework.data.jpa.repository.JpaRepository

interface JpaLikeRepository : JpaRepository<Like, Long>, KotlinJdslJpqlExecutor
