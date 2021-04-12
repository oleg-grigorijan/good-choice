package com.goodchoice.domain.subject.model

import com.fasterxml.jackson.annotation.JsonValue
import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.subject.MarkOutOfBoundsException

class Mark(@get:JsonValue val value: Int) {
    init {
        forbid(value in 1..5) { MarkOutOfBoundsException(value) }
    }
}