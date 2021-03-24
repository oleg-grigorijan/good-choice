package com.goodchoice.domain.config

import com.goodchoice.domain.auth.persistence.AuthRepository
import com.goodchoice.domain.auth.persistence.JooqAuthRepository
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthConfig {

    @Bean
    fun authRepo(db: DSLContext): AuthRepository = JooqAuthRepository(db)
}
