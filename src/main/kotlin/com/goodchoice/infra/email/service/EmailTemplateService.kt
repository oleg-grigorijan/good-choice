package com.goodchoice.infra.email.service

import com.goodchoice.infra.email.model.EmailMessageContent
import com.goodchoice.infra.email.model.EmailTemplateInput
import com.goodchoice.infra.email.model.EmailTemplateInput.EmployeeInvitation
import org.thymeleaf.context.Context
import org.thymeleaf.ITemplateEngine as TemplateEngine

interface EmailTemplateService {

    fun render(input: EmailTemplateInput): EmailMessageContent
}

class ThymeleafEmailTemplateService(private val templates: TemplateEngine) : EmailTemplateService {

    override fun render(input: EmailTemplateInput): EmailMessageContent =
        when (input) {
            is EmployeeInvitation -> EmailMessageContent(
                subject = "Employee invitation",
                body = renderBody("email/employee-invitation", input)
            )
        }

    @Suppress("SameParameterValue")
    private fun renderBody(templateName: String, input: EmailTemplateInput) =
        templates.process(templateName, Context().apply {
            setVariable("input", input)
        })
}
