package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.jooq.enums.ActorRole
import com.goodchoice.domain.common.model.Email
import org.jooq.DSLContext
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

class UserRepository(private val db: DSLContext) {

    fun existsByEmail(email: Email): Boolean =
        db.fetchExists(
            db.selectOne()
                .from(ACTOR)
                .where(ACTOR.EMAIL.eq(email.address))
        )
}

class ReviewerRepository(private val db: DSLContext, private val clock: Clock) {

    fun create(email: Email, firstName: String, lastName: String, password: String) {
        db.insertInto(ACTOR)
            .set(ACTOR.ID, UUID.randomUUID())
            .set(ACTOR.EMAIL, email.address)
            .set(ACTOR.FIRST_NAME, firstName)
            .set(ACTOR.LAST_NAME, lastName)
            .set(ACTOR.ROLE, ActorRole.REVIEWER)
            .set(ACTOR.PASSWORD, password)
            .set(ACTOR.CREATED_TIMESTAMP, LocalDateTime.now(clock))
            .execute()
    }
}
