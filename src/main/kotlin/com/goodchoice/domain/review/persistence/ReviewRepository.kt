package com.goodchoice.domain.review.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.common.jooq.Tables.*
import com.goodchoice.domain.common.jooq.enums.ReviewPointType.ADVANTAGE
import com.goodchoice.domain.common.jooq.enums.ReviewPointType.DISADVANTAGE
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.*
import com.goodchoice.domain.subject.model.Mark
import com.goodchoice.domain.user.model.UserPreview
import com.goodchoice.infra.common.now
import com.goodchoice.infra.persistence.read
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
        body: ReviewBodyCreationRequest,
        images: List<Reference>
    ): Reference

    fun vote(reviewId: UUID, issuerId: UUID, voteType: VoteType)
    fun getVotesByReviewIdOrNull(reviewId: UUID, reviewerId: UUID): ReviewVotes?
    fun removeVote(reviewId: UUID, reviewerId: UUID)
    fun getAllBySubject(
        subjectId: UUID,
        mark: Mark?,
        reviewerId: UUID?,
        filterNotOwn: Boolean,
        pageRequest: PageRequest
    ): Page<Review>

    fun getOwnBySubjectOrNull(subjectId: UUID, authorId: UUID?): Review?
}

class ReviewJooqRepository(
    private val db: DSLContext,
    private val clock: Clock,
    private val objectMapper: ObjectMapper
) : ReviewRepository {
    override fun create(
        title: String,
        author: Reference,
        subject: Reference,
        advantages: List<String>,
        disadvantages: List<String>,
        mark: Mark,
        body: ReviewBodyCreationRequest,
        images: List<Reference>
    ): Reference {
        val reviewId = UUID.randomUUID()
        val reviewBodyId = UUID.randomUUID()


        // todo: wrap NullPointerException thrown when a review from author to subject is already shown into business exception
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

        var pointsOrdering = 0
        db.insertInto(
            REVIEW_POINT,
            REVIEW_POINT.CONTENT,
            REVIEW_POINT.ORDERING,
            REVIEW_POINT.REVIEW_ID,
            REVIEW_POINT.TYPE
        )
            .apply { advantages.forEach { values(it, pointsOrdering++, reviewId, ADVANTAGE) } }
            .apply { disadvantages.forEach { values(it, pointsOrdering++, reviewId, DISADVANTAGE) } }
            .execute()

        var imageOrdering = 0;
        db.insertInto(
            REVIEW_IMAGE,
            REVIEW_IMAGE.REVIEW_ID,
            REVIEW_IMAGE.IMAGE_ID,
            REVIEW_IMAGE.ORDERING
        )
            .apply { images.forEach { values(reviewId, it.id, imageOrdering++) } }
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

    override fun getVotesByReviewIdOrNull(reviewId: UUID, reviewerId: UUID): ReviewVotes? {
        return db.selectFrom(GET_REVIEW_VOTES_BY_ACTOR(reviewerId))
            .where(GET_REVIEW_VOTES_BY_ACTOR.REVIEW_ID.eq(reviewId)).fetchOne()?.map {
                ReviewVotes(
                    it[GET_REVIEW_VOTES_BY_ACTOR.UPVOTES_COUNT],
                    it[GET_REVIEW_VOTES_BY_ACTOR.DOWNVOTES_COUNT],
                    it[GET_REVIEW_VOTES_BY_ACTOR.OWN_VOTE]?.let {
                        Vote(it.asVoteType())
                    }
                )
            }
    }

    override fun removeVote(reviewId: UUID, reviewerId: UUID) {
        db.deleteFrom(REVIEW_VOTE)
            .where(REVIEW_VOTE.REVIEW_ID.eq(reviewId).and(REVIEW_VOTE.REVIEWER_ID.eq(reviewerId)))
            .execute()
    }

    override fun getAllBySubject(
        subjectId: UUID,
        mark: Mark?,
        reviewerId: UUID?,
        filterNotOwn: Boolean,
        pageRequest: PageRequest
    ): Page<Review> {

        val limit = pageRequest.limit
        val offset = pageRequest.offset
        val items = db.selectFrom(GET_REVIEW_FULL_VIEW_BY_ACTOR(reviewerId))
            .where(
                GET_REVIEW_FULL_VIEW_BY_ACTOR.IS_SHOWN.eq(true)
                    .and(GET_REVIEW_FULL_VIEW_BY_ACTOR.SUBJECT_ID.eq(subjectId))
                    .let { condition ->
                        if (mark != null) {
                            condition.and(GET_REVIEW_FULL_VIEW_BY_ACTOR.MARK.eq(mark.value))
                        } else {
                            condition
                        }
                    }
                    .let { condition ->
                        if (filterNotOwn && reviewerId != null) {
                            condition.and(GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_ID.ne(reviewerId))
                        } else {
                            condition
                        }
                    }
            )
            .limit(limit + 1)
            .offset(offset)
            .fetch()
            .map {
                Review(
                    id = it[GET_REVIEW_FULL_VIEW_BY_ACTOR.ID],
                    title = it[GET_REVIEW_FULL_VIEW_BY_ACTOR.TITLE],
                    subject = Reference(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.SUBJECT_ID]),
                    author = UserPreview(
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_ID],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_FIRST_NAME],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_LAST_NAME]
                    ),
                    mark = Mark(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.MARK]),
                    advantages = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.ADVANTAGES]),
                    disadvantages = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.DISADVANTAGES]),
                    bodies = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.BODIES]),
                    votes = ReviewVotes(
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.UPVOTES_COUNT],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.DOWNVOTES_COUNT],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.OWN_VOTE]?.let {
                            Vote(it.asVoteType())
                        }
                    ),
                    images = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.IMAGES])
                )
            }

        var hasNext = false
        if (items.size == limit + 1) {
            items.removeLast()
            hasNext = true
        }
        return Page(offset, items, hasNext)
    }

    override fun getOwnBySubjectOrNull(subjectId: UUID, authorId: UUID?): Review? {
        return db.selectFrom(GET_REVIEW_FULL_VIEW_BY_ACTOR(authorId))
            .where(
                GET_REVIEW_FULL_VIEW_BY_ACTOR.SUBJECT_ID.eq(subjectId)
                    .and(GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_ID.eq(authorId))
            )
            .fetchOne()
            ?.map {
                Review(
                    id = it[GET_REVIEW_FULL_VIEW_BY_ACTOR.ID],
                    title = it[GET_REVIEW_FULL_VIEW_BY_ACTOR.TITLE],
                    subject = Reference(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.SUBJECT_ID]),
                    author = UserPreview(
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_ID],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_FIRST_NAME],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.AUTHOR_LAST_NAME]
                    ),
                    mark = Mark(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.MARK]),
                    advantages = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.ADVANTAGES]),
                    disadvantages = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.DISADVANTAGES]),
                    bodies = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.BODIES]),
                    votes = ReviewVotes(
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.UPVOTES_COUNT],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.DOWNVOTES_COUNT],
                        it[GET_REVIEW_FULL_VIEW_BY_ACTOR.OWN_VOTE]?.let {
                            Vote(it.asVoteType())
                        }
                    ),
                    images = objectMapper.read(it[GET_REVIEW_FULL_VIEW_BY_ACTOR.IMAGES])
                )
            }
    }
}
