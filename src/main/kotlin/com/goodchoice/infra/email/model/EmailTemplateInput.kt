package com.goodchoice.infra.email.model

sealed class EmailTemplateInput {

    data class EmployeeInvitation(
        private val token: String,
        val suggestedFirstName: String,
    ) : EmailTemplateInput() {

        fun getRegistrationUrl(webFrontendUrl: String) = "$webFrontendUrl/employeeInvitation?token=$token"
    }
}
