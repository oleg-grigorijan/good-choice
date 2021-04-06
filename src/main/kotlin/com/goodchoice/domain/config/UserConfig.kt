package com.goodchoice.domain.config

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.EmployeeCreationBootstrapProperties
import com.goodchoice.domain.user.persistence.*
import com.goodchoice.domain.user.service.*
import com.goodchoice.infra.email.service.EmailService
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@EnableConfigurationProperties(EmployeeCreationBootstrapProperties::class)
class UserConfig {

    @Bean
    fun reviewerRepo(db: DSLContext, clock: Clock): ReviewerRepository =
        JooqReviewerRepository(db, clock)

    @Bean
    fun userEmailConfirmationService(authService: AuthService): UserEmailConfirmationService =
        UserEmailConfirmationServiceImpl(authService)

    @Bean
    fun reviewerSelfService(
        reviewerRepo: ReviewerRepository,
        authService: AuthService,
        emailConfirmationService: UserEmailConfirmationService,
    ): ReviewerSelfService = ReviewerSelfServiceImpl(reviewerRepo, authService, emailConfirmationService)


    @Bean
    fun employeeRepository(db: DSLContext, clock: Clock): EmployeeRepository =
        JooqEmployeeRepository(db, clock)

    @Bean
    fun employeeInvitationRepo(db: DSLContext, clock: Clock): EmployeeInvitationRepository =
        JooqEmployeeInvitationRepository(db, clock)

    @Bean
    fun employeeInvitationService(
            employeeInvitationRepo: EmployeeInvitationRepository,
            emailService: EmailService,
            authService: AuthService,
            clock: Clock,
    ): EmployeeInvitationService = EmployeeInvitationServiceImpl(employeeInvitationRepo, emailService, authService, clock)

    @Bean
    fun employeeSelfService(
            employeeRepo: EmployeeRepository,
            employeeInvitationService: EmployeeInvitationService,
            authService: AuthService,
    ): EmployeeSelfService = EmployeeSelfServiceImpl(employeeRepo, employeeInvitationService, authService)

    @ConditionalOnProperty(name = ["good-choice.bootstrap.employee-creation.enabled"], havingValue = "true")
    @Bean(initMethod = "run")
    fun employeeCreateBootstrap(
            employeeRepo: EmployeeRepository,
            authService: AuthService,
            props: EmployeeCreationBootstrapProperties,
    ) = EmployeeCreationBootstrap(employeeRepo, authService, props.accounts)
}
