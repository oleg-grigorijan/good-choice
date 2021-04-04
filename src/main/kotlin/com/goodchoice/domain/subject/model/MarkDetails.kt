package com.goodchoice.domain.subject.model

import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.subject.MarkCountNegativeException

class MarkDetails(
    val value: Mark,
    val count: Int,
) {
    init {
        forbid(count < 0) { throw MarkCountNegativeException(count) }
    }
}