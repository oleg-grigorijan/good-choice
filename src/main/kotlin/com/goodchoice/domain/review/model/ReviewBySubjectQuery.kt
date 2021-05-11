package com.goodchoice.domain.review.model

import com.goodchoice.domain.subject.model.Mark
import java.util.*

data class ReviewBySubjectQuery(val subjectId: UUID, val filterNotOwn: Boolean, val mark: Mark?)