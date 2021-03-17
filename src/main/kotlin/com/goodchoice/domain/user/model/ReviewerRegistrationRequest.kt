package com.goodchoice.domain.user.model

import com.goodchoice.domain.common.model.Email
import com.goodchoice.infra.common.verify

class ReviewerRegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: Email,
    val password: String
) {
    init {
//        firstName = firstName.trim()
//
//        verify(firstName.isSmallString()) {}
    }
}
