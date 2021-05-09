package com.goodchoice.domain.review.service

import com.goodchoice.domain.auth.model.UserRole.REVIEWER
import com.goodchoice.domain.auth.model.requireRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.ReviewNotFoundException
import com.goodchoice.domain.review.model.Review
import com.goodchoice.domain.review.model.ReviewModificationRequest
import com.goodchoice.domain.review.model.ReviewVotes
import com.goodchoice.domain.review.model.Vote
import com.goodchoice.domain.review.persistence.ReviewRepository
import com.goodchoice.domain.subject.model.Mark
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ReviewService {
    fun create(request: ReviewModificationRequest): Reference
    fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotes
    fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotes
    fun getAllBySubject(subject: Reference, mark: Mark?, pageRequest: PageRequest): Page<Review>
}

class ReviewServiceImpl(private val reviewRepo: ReviewRepository, private val authService: AuthService) :
    ReviewService {

    @Transactional
    override fun create(request: ReviewModificationRequest): Reference {
        authService.currentAuth.requireRole(REVIEWER)
        return reviewRepo.create(
            title = request.title,
            author = Reference(authService.currentAuth.id),
            subject = request.subject,
            advantages = request.advantages,
            disadvantages = request.disadvantages,
            mark = request.mark,
            body = request.body
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
    override fun getAllBySubject(subject: Reference, mark: Mark?, pageRequest: PageRequest): Page<Review> =
        reviewRepo.getAllBySubject(subject.id, mark, authService.currentAuthOrNull?.id, pageRequest)
}