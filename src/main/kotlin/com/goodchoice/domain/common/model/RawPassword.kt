package com.goodchoice.domain.common.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.PASSWORD_MIN_LENGTH
import com.goodchoice.domain.common.PasswordMinLengthException
import com.goodchoice.domain.common.verify
import org.springframework.security.crypto.password.PasswordEncoder

data class RawPassword(@get:JsonValue val value: String) {

    init {
        verify(value.length >= PASSWORD_MIN_LENGTH) { PasswordMinLengthException(minLength = PASSWORD_MIN_LENGTH) }
    }
}

fun PasswordEncoder.encode(password: RawPassword): String = this.encode(password.value)
