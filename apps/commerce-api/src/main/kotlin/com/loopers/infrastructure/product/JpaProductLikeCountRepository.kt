package com.loopers.infrastructure.product

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.product.ProductLikeCount
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductLikeCountRepository : JpaRepository<ProductLikeCount, Long>, KotlinJdslJpqlExecutor
