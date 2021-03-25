package com.goodchoice.domain.user.model

import com.goodchoice.domain.common.isMediumString
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.Password
import com.goodchoice.domain.common.normalizeSingleLine
import com.goodchoice.infra.common.verify

class ReviewerRegistrationRequest(
    firstName: String,
    lastName: String,
    val email: Email,
    val password: Password
) {

    val firstName = firstName.normalizeSingleLine()
    val lastName = lastName.normalizeSingleLine()

    init {
        verify(firstName.isMediumString()) { RuntimeException() }
        verify(lastName.isMediumString()) { RuntimeException() }
    }
}
