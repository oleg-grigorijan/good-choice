package com.goodchoice.domain.subject.model

import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.subject.AverageMarkOfBoundsException
import com.goodchoice.domain.subject.ReviewsCountNegativeException

data class SubjectSummary(
    val reviewsCount: Int,
    val averageMark: Double,
    val marks: List<MarkDetails>
) {
    init {
        forbid(reviewsCount < 0) { throw ReviewsCountNegativeException(reviewsCount) }
        forbid((averageMark < 1) || (averageMark > 5)) { throw AverageMarkOfBoundsException(averageMark) }
    }
//    companion object{
//        fun empty():SubjectSummary = SubjectSummary(0, 0.0, emptyList())
//    }
}