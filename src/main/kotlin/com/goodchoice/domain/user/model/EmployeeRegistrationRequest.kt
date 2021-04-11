package com.goodchoice.domain.user.model

import com.goodchoice.StringMaxLength.MEDIUM
import com.goodchoice.domain.common.model.NewPassword
import com.goodchoice.domain.common.validateSingleLineString

data class EmployeeRegistrationRequest(
    val invitationToken: String,
    val firstName: String,
    val lastName: String,
    val password: NewPassword,
) {

    init {
        validateSingleLineString(firstName, maxLength = MEDIUM)
        validateSingleLineString(lastName, maxLength = MEDIUM)
    }
}
