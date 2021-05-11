package com.goodchoice.domain.review.service

import com.goodchoice.domain.auth.model.UserRole.REVIEWER
import com.goodchoice.domain.auth.model.requireAnyRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.ReviewNotFoundException
import com.goodchoice.domain.review.model.*
import com.goodchoice.domain.review.persistence.ReviewRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ReviewService {
    fun create(request: ReviewCreationRequest): Reference
    fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotes
    fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotes
    fun getAllBySubject(reviewBySubjectQuery: ReviewBySubjectQuery, pageRequest: PageRequest): Page<Review>
    fun getOwnBySubject(subject: Reference): Review
}

class ReviewServiceImpl(private val reviewRepo: ReviewRepository, private val authService: AuthService) :
    ReviewService {

    @Transactional
    override fun create(request: ReviewCreationRequest): Reference {
        authService.currentAuth.requireAnyRole(REVIEWER)
        return reviewRepo.create(
            title = request.title,
            author = Reference(authService.currentAuth.id),
            subject = request.subject,
            advantages = request.advantages,
            disadvantages = request.disadvantages,
            mark = request.mark,
            body = request.body,
            images = request.images
        )
    }

    @Transactional
    override fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotes {
        reviewRepo.vote(reviewId, authService.currentAuth.id, request.type)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }

    @Transactional
    override fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotes {
        reviewRepo.removeVote(reviewId, authService.currentAuth.id)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getAllBySubject(reviewBySubjectQuery: ReviewBySubjectQuery, pageRequest: PageRequest): Page<Review> =
        reviewRepo.getAllBySubject(
            reviewBySubjectQuery.subjectId,
            reviewBySubjectQuery.mark,
            authService.currentAuthOrNull?.id,
            reviewBySubjectQuery.filterNotOwn,
            pageRequest
        )

    @Transactional(readOnly = true)
    override fun getOwnBySubject(subject: Reference): Review {
        authService.currentAuth.requireAnyRole(REVIEWER)
        return reviewRepo.getOwnBySubjectOrNull(subject.id, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }
}