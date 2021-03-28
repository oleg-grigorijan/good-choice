package com.goodchoice.domain.common.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.EMAIL_MAX_LENGTH
import com.goodchoice.domain.common.InvalidEmailException
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.common.verify

data class Email(@get:JsonValue val address: String) {

    init {
        verify(address.count { it == '@' } == 1) { InvalidEmailException(address) }
        validateSingleLineString(address, maxLength = EMAIL_MAX_LENGTH)
    }
}
