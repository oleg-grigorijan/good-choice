package com.goodchoice.domain.subject.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.subjectTag.model.SubjectTag
import java.util.*

data class Subject(
    val id: UUID,
    val name: String,
    val brand: BrandPreview,
    val summary: SubjectSummary,
    val description: String,
    val tags: List<SubjectTag>
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}