package com.goodchoice.domain.user.model

import com.goodchoice.StringMaxLength.MEDIUM
import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.common.verify

data class EmployeeInvitationRequest(
        val role: UserRole,
        val email: Email,
        val suggestedFirstName: String,
        val suggestedLastName: String,
) {

    init {
        verify(role.isEmployee()) { RuntimeException() /* TODO(#8) */ }
        validateSingleLineString(suggestedFirstName, maxLength = MEDIUM)
        validateSingleLineString(suggestedLastName, maxLength = MEDIUM)
    }
}
