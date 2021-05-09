package com.goodchoice.domain.review.model

import java.net.URL
import java.util.*

data class ReviewImage(val id: UUID, val location: URL, val ordering: Int)