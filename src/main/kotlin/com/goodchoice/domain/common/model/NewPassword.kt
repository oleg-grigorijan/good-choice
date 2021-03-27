package com.goodchoice.domain.common.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.PASSWORD_MIN_LENGTH
import com.goodchoice.domain.common.WeakPasswordException
import com.goodchoice.domain.common.verify
import org.springframework.security.crypto.password.PasswordEncoder

data class NewPassword(@get:JsonValue val value: String) {

    init {
        verify(value.length >= PASSWORD_MIN_LENGTH) { WeakPasswordException() }
    }
}

fun PasswordEncoder.encode(password: NewPassword): String = this.encode(password.value)
