package com.goodchoice.domain.auth.persistence

import com.goodchoice.domain.auth.model.AuthView
import com.goodchoice.domain.auth.toUserRole
import com.goodchoice.domain.common.jooq.Tables.ACTOR
import org.jooq.DSLContext

interface AuthRepository {

    fun getByEmailOrNull(email: String): AuthView?
}

class JooqAuthRepository(private val db: DSLContext) : AuthRepository {

    override fun getByEmailOrNull(email: String): AuthView? =
        db.select(ACTOR.ID, ACTOR.ROLE, ACTOR.PASSWORD_HASH)
            .from(ACTOR)
            .where(ACTOR.EMAIL.eq(email))
            .fetchOne()
            ?.map { AuthView(it[ACTOR.ID], email, it[ACTOR.ROLE].toUserRole(), it[ACTOR.PASSWORD_HASH]) }
}
