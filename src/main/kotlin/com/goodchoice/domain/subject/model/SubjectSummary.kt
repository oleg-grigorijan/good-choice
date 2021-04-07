package com.goodchoice.domain.subject.model

import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.subject.AverageMarkOfBoundsException
import com.goodchoice.domain.subject.ReviewsCountNegativeException

data class SubjectSummary(
    val marks: List<MarkDetails>
) {

    val reviewsCount: Int
        get() = marks.map { it.count }.sum()

    val averageMark: Double?
        get() = (marks.map { it.value.value * it.count }.sum().toDouble() / reviewsCount).takeIf { it.isFinite() }

    init {
        forbid(reviewsCount < 0) { throw ReviewsCountNegativeException(reviewsCount) }
        //todo: come up with NaN solution and set appropriate range
        forbid((averageMark != null) && ((averageMark!! < 1.0) || (averageMark!! > 5))) {
            throw AverageMarkOfBoundsException(
                averageMark
            )
        }
    }
}