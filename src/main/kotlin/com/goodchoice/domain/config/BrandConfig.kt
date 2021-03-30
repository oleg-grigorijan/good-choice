package com.goodchoice.domain.config

import com.goodchoice.domain.brand.persistence.BrandJooqRepository
import com.goodchoice.domain.brand.persistence.BrandRepository
import com.goodchoice.domain.brand.service.BrandService
import com.goodchoice.domain.brand.service.BrandServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BrandConfig {
    @Bean
    fun brandService(brandRepo: BrandRepository): BrandService {
        return BrandServiceImpl(brandRepo)
    }

    @Bean
    fun brandRepo(db: DSLContext): BrandRepository {
        return BrandJooqRepository(db)
    }
}