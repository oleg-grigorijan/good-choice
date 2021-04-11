package com.goodchoice.infra.config

import com.goodchoice.domain.common.model.ExternalServicesProperties
import com.goodchoice.infra.email.model.EmailProperties
import com.goodchoice.infra.email.model.EmailTemplateCommonInput
import com.goodchoice.infra.email.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.ITemplateEngine as TemplateEngine

@Configuration
@EnableConfigurationProperties(EmailProperties::class)
class EmailConfig {

    @Bean
    fun emailTemplateCommonInput(externalServicesProps: ExternalServicesProperties): EmailTemplateCommonInput =
        EmailTemplateCommonInput(webFrontendUrl = externalServicesProps.webFrontend)

    @Bean
    fun emailTemplateService(
        templateEngine: TemplateEngine,
        commonInput: EmailTemplateCommonInput
    ): EmailTemplateService = ThymeleafEmailTemplateService(templateEngine, commonInput)

    @Bean
    @ConditionalOnProperty(name = ["good-choice.email.mode"], havingValue = "real")
    fun emailService(
        mailSender: JavaMailSender,
        emailTemplateService: EmailTemplateService,
        props: EmailProperties,
    ): EmailService = EmailServiceImpl(mailSender, emailTemplateService, props)

    @Bean
    @ConditionalOnProperty(name = ["good-choice.email.mode"], havingValue = "emulation")
    fun emailServiceEmulator(emailTemplateService: EmailTemplateService): EmailService =
        EmailServiceEmulator(emailTemplateService)
}
