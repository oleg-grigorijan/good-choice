package com.goodchoice.domain.subjectTag.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.validateSingleLineString

data class SubjectTagModificationRequest(
    val name: String
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}