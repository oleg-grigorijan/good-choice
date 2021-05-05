package com.goodchoice.domain.review.service

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.ReviewModificationRequest
import com.goodchoice.domain.review.persistence.ReviewRepository
import org.springframework.transaction.annotation.Transactional

interface ReviewService {
    fun create(request: ReviewModificationRequest): Reference
//    fun getById(id: UUID): Review
//    fun update(id: UUID, request: ReviewModificationRequest)
//    fun getAllPreviewsByQuery(subjectQuery: SubjectQuery, pageRequest: PageRequest): Page<SubjectPreview>

}

class ReviewServiceImpl(private val reviewRepo: ReviewRepository) : ReviewService {
    @Transactional
    override fun create(request: ReviewModificationRequest): Reference =
        reviewRepo.create(
            title = request.title,
            author = request.author,
            subject = request.subject,
            advantages = request.advantages,
            disadvantages = request.disadvantages,
            mark = request.mark,
            body = request.body
        )

}