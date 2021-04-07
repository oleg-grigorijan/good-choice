package com.goodchoice.domain.subject.model

import java.util.*

data class SubjectQuery(
    val query: String?,
    val brandId: UUID?,
    val tagId: UUID?,
)