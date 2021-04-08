package com.goodchoice.domain.subject.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.common.validateSingleLineString

class SubjectModificationRequest(
    val name: String,
    val description: String,
    val brand: Reference,
    val addedSubjectTags: List<Reference>,
    val removedSubjectTags: List<Reference>
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}