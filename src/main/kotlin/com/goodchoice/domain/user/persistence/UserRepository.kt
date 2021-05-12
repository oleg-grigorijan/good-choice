package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.auth.model.toUserRole
import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.user.model.User
import org.jooq.DSLContext

interface UserRepository {
    fun getByEmailOrNull(email: Email): User?
}

class UserRepositoryImpl(private val db: DSLContext) : UserRepository {
    override fun getByEmailOrNull(email: Email): User? =
        db.selectFrom(ACTOR)
            .where(ACTOR.EMAIL.eq(email.address).and(ACTOR.IS_ACTIVE))
            .fetchOne()
            ?.map {
                User(
                    id = it[ACTOR.ID],
                    firstName = it[ACTOR.FIRST_NAME],
                    lastName = it[ACTOR.LAST_NAME],
                    email = Email(it[ACTOR.EMAIL]),
                    role = it[ACTOR.ROLE].toUserRole(),
                    passwordHash = it[ACTOR.PASSWORD_HASH],
                    profileImage = it[ACTOR.PROFILE_IMAGE_ID]?.let { Reference(it) },
                    createdTimeStamp = it[ACTOR.CREATED_TIMESTAMP]
                )
            }
}