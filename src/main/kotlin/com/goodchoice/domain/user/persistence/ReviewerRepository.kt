package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.jooq.enums.ActorRole
import org.jooq.DSLContext
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

interface ReviewerRepository {

    fun create(firstName: String, lastName: String, passwordHash: String): UUID
}

class JooqReviewerRepository(private val db: DSLContext, private val clock: Clock) : ReviewerRepository {

    override fun create(firstName: String, lastName: String, passwordHash: String): UUID {
        val id = UUID.randomUUID()
        db.insertInto(ACTOR)
            .set(ACTOR.ID, id)
            .set(ACTOR.FIRST_NAME, firstName)
            .set(ACTOR.LAST_NAME, lastName)
            .set(ACTOR.ROLE, ActorRole.REVIEWER)
            .set(ACTOR.PASSWORD_HASH, passwordHash)
            .set(ACTOR.CREATED_TIMESTAMP, LocalDateTime.now(clock))
            .set(ACTOR.IS_ACTIVE, true)
            .execute()
        return id
    }
}
