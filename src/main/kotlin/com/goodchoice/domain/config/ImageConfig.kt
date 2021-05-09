package com.goodchoice.domain.config

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.image.persistence.ImageRepository
import com.goodchoice.domain.image.persistence.ImageRepositoryImpl
import com.goodchoice.domain.image.service.ImageServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ImageConfig {
    @Bean
    fun imageRepo(db: DSLContext): ImageRepository = ImageRepositoryImpl(db)

    @Bean
    fun imageService(imageRepo: ImageRepository, authService: AuthService) = ImageServiceImpl(imageRepo, authService)
}