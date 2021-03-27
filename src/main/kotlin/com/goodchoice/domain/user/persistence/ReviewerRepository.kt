package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.jooq.enums.ActorRole
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

@Repository
class ReviewerRepository(private val db: DSLContext, private val clock: Clock) {

    fun create(firstName: String, lastName: String, passwordHash: String): UUID {
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
