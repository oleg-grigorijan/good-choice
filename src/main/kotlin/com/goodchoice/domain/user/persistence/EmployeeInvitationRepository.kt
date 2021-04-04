package com.goodchoice.domain.user.persistence

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.auth.model.toJooqActorRole
import com.goodchoice.domain.auth.model.toUserRole
import com.goodchoice.domain.common.jooq.Tables.EMPLOYEE_INVITATION
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.user.EmployeeInvitationNotFoundException
import com.goodchoice.domain.user.model.EmployeeInvitation
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID

interface EmployeeInvitationRepository {

    fun getByTokenOrNull(token: String): EmployeeInvitation?
    fun getAll(): List<EmployeeInvitation>
    fun create(
        token: String,
        email: Email,
        role: UserRole,
        suggestedFirstName: String,
        suggestedLastName: String,
        expiredTimestamp: LocalDateTime,
    ): EmployeeInvitation

    fun refresh(id: UUID, refreshedToken: String, expiredTimestamp: LocalDateTime): EmployeeInvitation
    fun remove(id: UUID)
    fun removeByToken(token: String)
}

class JooqEmployeeInvitationRepository(private val db: DSLContext, private val clock: Clock) : EmployeeInvitationRepository {

    private val modelFields = with(EMPLOYEE_INVITATION) {
        listOf(ID, EMAIL, ROLE, SUGGESTED_FIRST_NAME, SUGGESTED_LAST_NAME, EXPIRED_TIMESTAMP)
    }

    override fun getByTokenOrNull(token: String): EmployeeInvitation? =
        db.select(modelFields)
            .from(EMPLOYEE_INVITATION)
            .where(EMPLOYEE_INVITATION.TOKEN.eq(token))
            .fetchOne()
            ?.mapToEmployeeInvitation()

    override fun getAll(): List<EmployeeInvitation> =
        db.select(modelFields)
            .from(EMPLOYEE_INVITATION)
            .fetch()
            .mapToEmployeeInvitations()

    override fun create(
        token: String,
        email: Email,
        role: UserRole,
        suggestedFirstName: String,
        suggestedLastName: String,
        expiredTimestamp: LocalDateTime,
    ): EmployeeInvitation =

        with(EMPLOYEE_INVITATION) {
            db.insertInto(EMPLOYEE_INVITATION)
                .set(ID, randomUUID())
                .set(TOKEN, token)
                .set(EMAIL, email.address)
                .set(ROLE, role.toJooqActorRole())
                .set(SUGGESTED_FIRST_NAME, suggestedFirstName)
                .set(SUGGESTED_LAST_NAME, suggestedLastName)
                .apply {
                    val now = now(clock)
                    set(CREATED_TIMESTAMP, now)
                    set(LAST_REFRESHED_TIMESTAMP, now)
                }
                .set(EXPIRED_TIMESTAMP, expiredTimestamp)
                .returning(modelFields)
                .fetchOne()!!
                .mapToEmployeeInvitation()
        }

    override fun refresh(id: UUID, refreshedToken: String, expiredTimestamp: LocalDateTime): EmployeeInvitation =
        with(EMPLOYEE_INVITATION) {
            db.update(EMPLOYEE_INVITATION)
                .set(TOKEN, refreshedToken)
                .set(LAST_REFRESHED_TIMESTAMP, now(clock))
                .set(EXPIRED_TIMESTAMP, expiredTimestamp)
                .where(ID.eq(id))
                .returning(modelFields)
                .fetchOne()
                ?.mapToEmployeeInvitation()
                ?: throw EmployeeInvitationNotFoundException()
        }

    override fun remove(id: UUID) {
        db.deleteFrom(EMPLOYEE_INVITATION)
            .where(EMPLOYEE_INVITATION.ID.eq(id))
            .execute()
    }

    override fun removeByToken(token: String) {
        db.deleteFrom(EMPLOYEE_INVITATION)
            .where(EMPLOYEE_INVITATION.TOKEN.eq(token))
            .execute()
    }

    private fun Record.mapToEmployeeInvitation(): EmployeeInvitation =
        with(EMPLOYEE_INVITATION) {
            EmployeeInvitation(
                id = get(ID),
                email = Email(get(EMAIL)),
                role = get(ROLE).toUserRole(),
                suggestedFirstName = get(SUGGESTED_FIRST_NAME),
                suggestedLastName = get(SUGGESTED_LAST_NAME),
                expiredTimestamp = get(EXPIRED_TIMESTAMP),
            )
        }

    private fun Result<Record>.mapToEmployeeInvitations(): List<EmployeeInvitation> =
        map { it.mapToEmployeeInvitation() }
}

