package com.goodchoice.domain.auth.persistence

import com.goodchoice.domain.auth.model.Auth
import com.goodchoice.domain.auth.model.AuthWithCredentials
import com.goodchoice.domain.auth.model.toUserRole
import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.model.Email
import org.jooq.DSLContext
import java.util.*

interface AuthRepository {

    fun existsByEmail(email: Email): Boolean
    fun getCredentialsByEmailOrNull(email: Email): AuthWithCredentials?
    fun updateEmailByUser(userId: UUID, email: Email)
    fun getByCredentialsOrNull(email: Email, password: String): Auth?
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
            ?.map { AuthWithCredentials(it[ACTOR.ID], email, it[ACTOR.ROLE].toUserRole(), it[ACTOR.PASSWORD_HASH]) }

    override fun updateEmailByUser(userId: UUID, email: Email) {
        db.update(ACTOR)
            .set(ACTOR.EMAIL, email.address)
            .where(ACTOR.ID.eq(userId))
            .execute()
    }

    override fun getByCredentialsOrNull(email: Email, password: String): Auth? {
        TODO("Not yet implemented")
    }
}
