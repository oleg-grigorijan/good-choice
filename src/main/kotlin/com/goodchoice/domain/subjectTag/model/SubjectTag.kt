package com.goodchoice.domain.subjectTag.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.validateSingleLineString
import java.util.*

class SubjectTag(
    val id: UUID,
    val name: String,
    val subjectsCount: Int
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}