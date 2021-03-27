package com.goodchoice.domain.config

import com.goodchoice.domain.auth.persistence.AuthRepository
import com.goodchoice.domain.auth.persistence.JooqAuthRepository
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.auth.service.AuthServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authRepo(db: DSLContext): AuthRepository = JooqAuthRepository(db)

    @Bean
    fun authService(authRepo: AuthRepository, passwordEncoder: PasswordEncoder): AuthService =
        AuthServiceImpl(authRepo, passwordEncoder)
}
