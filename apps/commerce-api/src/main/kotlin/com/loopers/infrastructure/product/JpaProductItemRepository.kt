package com.loopers.infrastructure.product

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.product.ProductItem
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductItemRepository : JpaRepository<ProductItem, Long>, KotlinJdslJpqlExecutor
