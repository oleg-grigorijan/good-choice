package com.goodchoice.domain.user.model

import com.goodchoice.StringMaxLength.MEDIUM
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.NewPassword
import com.goodchoice.domain.common.validateSingleLineString

class ReviewerRegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: Email,
    val password: NewPassword
) {

    init {
        validateSingleLineString(firstName, maxLength = MEDIUM)
        validateSingleLineString(lastName, maxLength = MEDIUM)
    }
}
