package com.goodchoice.domain.review.persistence

import com.goodchoice.domain.common.jooq.Tables.*
import com.goodchoice.domain.common.jooq.enums.ReviewPointType.ADVANTAGE
import com.goodchoice.domain.common.jooq.enums.ReviewPointType.DISADVANTAGE
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.*
import com.goodchoice.domain.subject.model.Mark
import com.goodchoice.infra.common.now
import org.jooq.DSLContext
import java.time.Clock
import java.util.*

interface ReviewRepository {
    fun create(
        title: String,
        author: Reference,
        subject: Reference,
        advantages: List<String>,
        disadvantages: List<String>,
        mark: Mark,
        body: ReviewBodyCreationRequest
    ): Reference

    fun vote(reviewId: UUID, issuerId: UUID, voteType: VoteType)
    fun getVotesByReviewIdOrNull(reviewId: UUID, issuerId: UUID): ReviewVotes?
    fun removeVote(reviewId: UUID, issuerId: UUID)
}

class ReviewJooqRepository(private val db: DSLContext, private val clock: Clock) : ReviewRepository {
    override fun create(
        title: String,
        author: Reference,
        subject: Reference,
        advantages: List<String>,
        disadvantages: List<String>,
        mark: Mark,
        body: ReviewBodyCreationRequest
    ): Reference {
        val reviewId = UUID.randomUUID()
        val reviewBodyId = UUID.randomUUID()

        //todo: fix NullPointer on .execute() when author is administrator
        db.insertInto(REVIEW)
            .set(REVIEW.ID, reviewId)
            .set(REVIEW.TITLE, title)
            .set(REVIEW.SUBJECT_ID, subject.id)
            .set(REVIEW.MARK, mark.value)
            .set(REVIEW.REVIEWER_ID, author.id)
            .set(REVIEW.IS_SHOWN, true)
            .execute()

        db.insertInto(REVIEW_BODY)
            .set(REVIEW_BODY.ID, reviewBodyId)
            .set(REVIEW_BODY.CONTENT, body.content)
            .set(REVIEW_BODY.CREATED_TIMESTAMP, clock.now())
            .set(REVIEW_BODY.REVIEW_ID, reviewId)
            .execute()

        var ordering = 0
        db.insertInto(
            REVIEW_POINT,
            REVIEW_POINT.CONTENT,
            REVIEW_POINT.ORDERING,
            REVIEW_POINT.REVIEW_ID,
            REVIEW_POINT.TYPE
        ).apply { advantages.forEach { values(it, ordering++, reviewId, ADVANTAGE) } }
            .apply { disadvantages.forEach { values(it, ordering++, reviewId, DISADVANTAGE) } }
            .execute()

        return Reference(reviewId)
    }

    override fun vote(reviewId: UUID, issuerId: UUID, voteType: VoteType) {
        db.insertInto(REVIEW_VOTE, REVIEW_VOTE.REVIEWER_ID, REVIEW_VOTE.REVIEW_ID, REVIEW_VOTE.TYPE)
            .values(issuerId, reviewId, voteType.asJooqVoteType())
            .onDuplicateKeyUpdate()
            .set(REVIEW_VOTE.REVIEWER_ID, issuerId)
            .set(REVIEW_VOTE.REVIEW_ID, reviewId)
            .set(REVIEW_VOTE.TYPE, voteType.asJooqVoteType())
            .execute()
    }

    override fun getVotesByReviewIdOrNull(reviewId: UUID, issuerId: UUID): ReviewVotes? {

        //todo: check when selectCount fetches null

        //returns null when no reviews with reviewId
        db.selectFrom(REVIEW).where(REVIEW.ID.eq(reviewId)).fetchOne() ?: return null

        val upvotesCount = db.selectCount().from(REVIEW_VOTE)
            .where(REVIEW_VOTE.REVIEW_ID.eq(reviewId))
            .and(REVIEW_VOTE.TYPE.eq(VoteType.UP.asJooqVoteType()))
            .fetchOne(0, Int::class.java) ?: 0

        val downvotesCount = db.selectCount().from(REVIEW_VOTE)
            .where(REVIEW_VOTE.REVIEW_ID.eq(reviewId))
            .and(REVIEW_VOTE.TYPE.eq(VoteType.DOWN.asJooqVoteType()))
            .fetchOne(0, Int::class.java) ?: 0

        val own = db.selectFrom(REVIEW_VOTE)
            .where(REVIEW_VOTE.REVIEW_ID.eq(reviewId))
            .and(REVIEW_VOTE.REVIEWER_ID.eq(issuerId))
            .fetchOne()
            ?.map { Vote(it[REVIEW_VOTE.TYPE].asVoteType()) }

        return ReviewVotes(upvotesCount, downvotesCount, own)

    }

    override fun removeVote(reviewId: UUID, issuerId: UUID) {
        db.deleteFrom(REVIEW_VOTE)
            .where(REVIEW_VOTE.REVIEW_ID.eq(reviewId))
            .and(REVIEW_VOTE.REVIEWER_ID.eq(issuerId))
            .execute()
    }
}