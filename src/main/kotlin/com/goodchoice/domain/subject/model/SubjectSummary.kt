package com.goodchoice.domain.subject.model

data class SubjectSummary(
    val marks: List<MarkDetails>
) {

    val reviewsCount: Int
        get() = marks.map { it.count }.sum()

    val averageMark: Double?
        get() = marks.map { it.value.value * it.count }.sum().toDouble().div(reviewsCount).takeIf { it.isFinite() }

}