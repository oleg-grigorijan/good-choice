package com.goodchoice.domain.common.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.domain.common.exception.FormatException
import com.goodchoice.infra.common.verify
import org.springframework.security.crypto.password.PasswordEncoder

data class Password(@get:JsonValue val value: String) {

    init {
        verify(value.length >= 8) { WeakPasswordException() }
    }
}

class WeakPasswordException : FormatException("")

fun PasswordEncoder.encode(password: Password): String = this.encode(password.value)
