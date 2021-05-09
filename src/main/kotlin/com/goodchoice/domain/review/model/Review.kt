package com.goodchoice.domain.review.model

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.Mark
import com.goodchoice.domain.user.model.UserPreview
import java.util.*

data class Review(
    val id: UUID,
    val title: String,
    val subject: Reference,
    val author: UserPreview,
    val mark: Mark,
    val advantages: List<String>,
    val disadvantages: List<String>,
    val bodies: List<ReviewBody>,
    val votes: ReviewVotes,
    val images:
)