package com.goodchoice.domain.config

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.subjectTag.persistence.SubjectTagJooqRepository
import com.goodchoice.domain.subjectTag.persistence.SubjectTagRepository
import com.goodchoice.domain.subjectTag.service.SubjectTagService
import com.goodchoice.domain.subjectTag.service.SubjectTagServiceImpl
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SubjectTagConfig {
    @Bean
    fun tagService(subjectTagRepo: SubjectTagRepository, authService: AuthService): SubjectTagService =
        SubjectTagServiceImpl(subjectTagRepo, authService)

    @Bean
    fun tagRepo(db: DSLContext): SubjectTagRepository =
        SubjectTagJooqRepository(db)
}