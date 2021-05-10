package com.goodchoice.domain.subject.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.image.model.Image
import com.goodchoice.domain.image.model.OrderedImage
import com.goodchoice.domain.subjectTag.model.SubjectTag
import java.util.*

data class Subject(
    val id: UUID,
    val name: String,
    val brand: BrandPreview,
    val summary: SubjectSummary,
    val description: String,
    val subjectTags: List<SubjectTag>,
    val images: List<OrderedImage>,
    val primaryImage: Image?
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}