package com.goodchoice.domain.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.review.persistence.ReviewJooqRepository
import com.goodchoice.domain.review.persistence.ReviewRepository
import com.goodchoice.domain.review.service.ReviewService
import com.goodchoice.domain.review.service.ReviewServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class ReviewConfig {
    @Bean
    fun reviewService(reviewRepository: ReviewRepository, authService: AuthService): ReviewService =
        ReviewServiceImpl(reviewRepository, authService)

    @Bean
    fun reviewRepo(db: DSLContext, clock: Clock, objectMapper: ObjectMapper): ReviewRepository =
        ReviewJooqRepository(db, clock, objectMapper)
}