package com.goodchoice.domain.review.service

import com.goodchoice.domain.auth.service.AuthContext
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.ReviewNotFoundException
import com.goodchoice.domain.review.model.ReviewModificationRequest
import com.goodchoice.domain.review.model.ReviewVotes
import com.goodchoice.domain.review.model.Vote
import com.goodchoice.domain.review.persistence.ReviewRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ReviewService {
    fun create(request: ReviewModificationRequest): Reference
    fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotes
    fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotes
//    fun getById(id: UUID): Review
//    fun update(id: UUID, request: ReviewModificationRequest)
//    fun getAllPreviewsByQuery(subjectQuery: SubjectQuery, pageRequest: PageRequest): Page<SubjectPreview>

}

class ReviewServiceImpl(private val reviewRepo: ReviewRepository, private val authContext: AuthContext) :
    ReviewService {

    @Transactional
    override fun create(request: ReviewModificationRequest): Reference =
        reviewRepo.create(
            title = request.title,
            author = Reference(authContext.currentAuth.id),
            subject = request.subject,
            advantages = request.advantages,
            disadvantages = request.disadvantages,
            mark = request.mark,
            body = request.body
        )

    @Transactional
    override fun voteByAuthenticatedUser(reviewId: UUID, request: Vote): ReviewVotes {
        reviewRepo.vote(reviewId, authContext.currentAuth.id, request.type)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authContext.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }

    @Transactional
    override fun removeAuthenticatedUserVote(reviewId: UUID): ReviewVotes {
        reviewRepo.removeVote(reviewId, authContext.currentAuth.id)
        return reviewRepo.getVotesByReviewIdOrNull(reviewId, authContext.currentAuth.id)
            ?: throw ReviewNotFoundException()
    }
}