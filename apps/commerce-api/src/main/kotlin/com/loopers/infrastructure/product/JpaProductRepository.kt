package com.loopers.infrastructure.product

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.product.Product
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductRepository : JpaRepository<Product, Long>, KotlinJdslJpqlExecutor
