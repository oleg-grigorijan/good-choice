package com.goodchoice.domain.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.subject.persistence.SubjectJooqRepository
import com.goodchoice.domain.subject.persistence.SubjectRepository
import com.goodchoice.domain.subject.service.SubjectService
import com.goodchoice.domain.subject.service.SubjectServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class SubjectConfig {
    @Bean
    fun subjectService(subjectRepo: SubjectRepository, authService: AuthService): SubjectService =
        SubjectServiceImpl(subjectRepo, authService)

    @Bean
    fun subjectRepo(db: DSLContext, clock: Clock, objectMapper: ObjectMapper): SubjectRepository =
        SubjectJooqRepository(db, clock, objectMapper)
}