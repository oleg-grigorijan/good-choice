package com.goodchoice.domain.config

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.persistence.ReviewerRepository
import com.goodchoice.domain.user.service.ReviewerSelfService
import com.goodchoice.domain.user.service.ReviewerSelfServiceImpl
import com.goodchoice.domain.user.service.UserEmailConfirmationService
import com.goodchoice.domain.user.service.UserEmailConfirmationServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserConfig {

    @Bean
    fun userEmailConfirmationService(authService: AuthService): UserEmailConfirmationService =
        UserEmailConfirmationServiceImpl(authService)

    @Bean
    fun reviewerSelfService(
        reviewerRepo: ReviewerRepository,
        authService: AuthService,
        emailConfirmationService: UserEmailConfirmationService
    ): ReviewerSelfService = ReviewerSelfServiceImpl(reviewerRepo, authService, emailConfirmationService)
}
