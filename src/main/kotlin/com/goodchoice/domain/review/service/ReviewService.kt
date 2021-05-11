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
import com.goodchoice.domain.subject.model.Mark
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ReviewService {
    fun create(request: ReviewCreationRequest): Reference
    fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotesWithOwn
    fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotesWithOwn
    fun getAllBySubject(subject: Reference, mark: Mark?, pageRequest: PageRequest): Page<Review>
    fun getBySubjectAndAuthenticatedAuthor(subject: Reference): OwnReview
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
    override fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotesWithOwn {
        reviewRepo.vote(reviewId, authService.currentAuth.id, request.type)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }

    @Transactional
    override fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotesWithOwn {
        reviewRepo.removeVote(reviewId, authService.currentAuth.id)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getAllBySubject(subject: Reference, mark: Mark?, pageRequest: PageRequest): Page<Review> =
        reviewRepo.getAllBySubject(subject.id, mark, authService.currentAuthOrNull?.id, pageRequest)

    @Transactional(readOnly = true)
    override fun getBySubjectAndAuthenticatedAuthor(subject: Reference): OwnReview {
        authService.currentAuth.requireAnyRole(REVIEWER)
        return reviewRepo.getBySubjectAndAuthorOrNull(subject.id, authService.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }
}