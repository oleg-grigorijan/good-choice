package com.goodchoice.domain.review.model

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.Mark

data class ReviewModificationRequest(
    val title: String,
    val author: Reference,
    val subject: Reference,
    val advantages: List<String>,
    val disadvantages: List<String>,
    val mark: Mark,
    val body: ReviewBodyCreationRequest
)