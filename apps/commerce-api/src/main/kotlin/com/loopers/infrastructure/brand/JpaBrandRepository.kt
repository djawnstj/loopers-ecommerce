package com.loopers.infrastructure.brand

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.brand.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBrandRepository : JpaRepository<Brand, Long>, KotlinJdslJpqlExecutor
