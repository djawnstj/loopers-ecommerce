package com.loopers.config.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = ["com.loopers.**.domain"])
@EnableJpaRepositories(basePackages = ["com.loopers.**.infrastructure"])
class JpaConfig
