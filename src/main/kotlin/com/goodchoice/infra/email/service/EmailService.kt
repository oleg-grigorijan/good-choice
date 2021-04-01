package com.goodchoice.infra.email.service

import com.goodchoice.domain.common.model.Email
import com.goodchoice.infra.email.model.EmailProperties
import com.goodchoice.infra.email.model.EmailTemplateInput
import mu.KotlinLogging.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator

interface EmailService {

    fun send(to: Email, templateInput: EmailTemplateInput)
}

class EmailServiceImpl(
    private val emailSender: JavaMailSender,
    private val templateService: EmailTemplateService,
    private val props: EmailProperties,
) : EmailService {

    override fun send(to: Email, templateInput: EmailTemplateInput) {
        val (subject, body) = templateService.render(templateInput)

        MimeMessagePreparator { message ->
            MimeMessageHelper(message).apply {
                setFrom(props.bot.address, props.bot.name)
                setTo(to.address)
                setReplyTo(props.help.address, props.help.name)
                setSubject(subject)
                setText(body, true)
            }
        }.let { emailSender.send(it) }
    }
}

class EmailServiceEmulator(private val templateService: EmailTemplateService) : EmailService {

    private val log = logger {}

    override fun send(to: Email, templateInput: EmailTemplateInput) {
        log.info { "${templateService.render(templateInput)} to $to sending emulation" }
    }
}
