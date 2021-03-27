package com.goodchoice.domain.auth.persistence

import com.goodchoice.domain.auth.model.AuthWithCredentials
import com.goodchoice.domain.auth.model.toUserRole
import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.verify
import org.jooq.DSLContext
import java.util.*

interface AuthRepository {

    fun existsByEmail(email: Email): Boolean
    fun getCredentialsByEmailOrNull(email: Email): AuthWithCredentials?
    fun updateEmailByUser(userId: UUID, email: Email)
}

class JooqAuthRepository(private val db: DSLContext) : AuthRepository {

    override fun existsByEmail(email: Email): Boolean =
        db.fetchExists(
            db.selectOne()
                .from(ACTOR)
                .where(ACTOR.EMAIL.eq(email.address))
        )

    override fun getCredentialsByEmailOrNull(email: Email): AuthWithCredentials? =
        db.select(ACTOR.ID, ACTOR.ROLE, ACTOR.PASSWORD_HASH)
            .from(ACTOR)
            .where(ACTOR.EMAIL.eq(email.address))
            .fetchOne()
            ?.map { AuthWithCredentials(it[ACTOR.ID], email.address, it[ACTOR.ROLE].toUserRole(), it[ACTOR.PASSWORD_HASH]) }

    override fun updateEmailByUser(userId: UUID, email: Email) {
        db.update(ACTOR)
            .set(ACTOR.EMAIL, email.address)
            .execute()
            .also { changedRecordsCount -> verify(changedRecordsCount == 1) { RuntimeException() } }
    }
}
