package com.goodchoice.domain.subject.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.domain.common.verify
import com.goodchoice.domain.subject.MarkOutOfBoundsException

class Mark(@get:JsonValue val value: Int) {
    init {
        verify(value in 1..5) { MarkOutOfBoundsException(value) }
    }
}