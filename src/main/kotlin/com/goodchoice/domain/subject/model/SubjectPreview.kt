package com.goodchoice.domain.subject.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.image.model.ImageDescriptor
import com.goodchoice.domain.subjectTag.model.SubjectTag
import java.util.*

data class SubjectPreview(
    val id: UUID,
    val name: String,
    val brand: BrandPreview,
    val summary: SubjectSummary,
    val subjectTags: List<SubjectTag>,
    val primaryImage: ImageDescriptor?
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}