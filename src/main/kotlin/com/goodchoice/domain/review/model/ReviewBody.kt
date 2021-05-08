package com.goodchoice.domain.review.model

import java.time.LocalDateTime
import java.util.*

data class ReviewBody(val id: UUID, val content: String, val createdTimestamp: LocalDateTime)