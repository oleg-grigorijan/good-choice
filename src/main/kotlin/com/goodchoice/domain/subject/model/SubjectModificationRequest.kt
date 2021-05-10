package com.goodchoice.domain.subject.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.common.validateSingleLineString
import com.goodchoice.domain.image.model.OrderedImageModificationRequest
import com.goodchoice.domain.image.model.OrderedImageRemoveRequest

data class SubjectModificationRequest(
    val name: String,
    val description: String,
    val brand: Reference,
    val addedTags: List<Reference>,
    val removedTags: List<Reference>,
    val addedImages: List<OrderedImageModificationRequest>,
    val removedImages: List<OrderedImageRemoveRequest>,
    val primaryImage: Reference?
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}