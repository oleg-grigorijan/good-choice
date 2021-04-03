package com.goodchoice.infra.email.model

sealed class EmailTemplateInput {

    data class EmployeeInvitation(
        private val token: String,
        val suggestedFirstName: String,
        private val webFrontendUrl: String,
    ) : EmailTemplateInput() {

        val registrationUrl
            get() = "$webFrontendUrl/employeeInvitation?token=$token"
    }
}
