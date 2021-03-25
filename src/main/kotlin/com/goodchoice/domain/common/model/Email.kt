package com.goodchoice.domain.common.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.domain.common.exception.FormatException
import com.goodchoice.infra.common.verify

data class Email(@get:JsonValue val address: String) {

    init {
        verify(address.count { it == '@' } == 1) { InvalidEmailException() }
    }
}

class InvalidEmailException : FormatException("")
