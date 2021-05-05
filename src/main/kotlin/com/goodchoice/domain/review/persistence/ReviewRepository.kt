package com.goodchoice.domain.review.persistence

import com.goodchoice.domain.common.jooq.Tables
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.ReviewBodyCreationRequest
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

        db.insertInto(Tables.REVIEW)
            .set(Tables.REVIEW.ID, reviewId)
            .set(Tables.REVIEW.TITLE, title)
            .set(Tables.REVIEW.SUBJECT_ID, subject.id)
            .set(Tables.REVIEW.MARK, mark.value)
            .set(Tables.REVIEW.IS_SHOWN, true)
            .execute()

        db.insertInto(Tables.REVIEW_BODY)
            .set(Tables.REVIEW_BODY.ID, reviewBodyId)
            .set(Tables.REVIEW_BODY.CONTENT, body.content)
            .set(Tables.REVIEW_BODY.CREATED_TIMESTAMP, clock.now())
            .set(Tables.REVIEW_BODY.REVIEW_ID, reviewId)
            .execute()

        return Reference(reviewId)
    }

}