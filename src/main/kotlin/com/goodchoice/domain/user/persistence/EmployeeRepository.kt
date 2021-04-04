package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.auth.model.toJooqActorRole
import com.goodchoice.domain.common.jooq.Tables.ACTOR
import com.goodchoice.domain.common.model.Email
import org.jooq.DSLContext
import java.time.Clock
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

interface EmployeeRepository {

    fun create(role: UserRole, email: Email, firstName: String, lastName: String, passwordHash: String)
}

class JooqEmployeeRepository(private val db: DSLContext, private val clock: Clock) : EmployeeRepository {

    override fun create(role: UserRole, email: Email, firstName: String, lastName: String, passwordHash: String) {
        with(ACTOR) {
            db.insertInto(ACTOR)
                .set(ID, randomUUID())
                .set(FIRST_NAME, firstName)
                .set(LAST_NAME, lastName)
                .set(EMAIL, email.address)
                .set(ROLE, role.toJooqActorRole())
                .set(PASSWORD_HASH, passwordHash)
                .set(IS_ACTIVE, true)
                .set(CREATED_TIMESTAMP, now(clock))
                .execute()
        }
    }
}
