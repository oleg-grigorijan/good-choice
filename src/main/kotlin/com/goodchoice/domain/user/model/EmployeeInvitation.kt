package com.goodchoice.domain.user.model

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.model.Email
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

data class EmployeeInvitation(
    val id: UUID,
    val email: Email,
    val role: UserRole,
    override val suggestedFirstName: String,
    override val suggestedLastName: String,
    val expiredTimestamp: LocalDateTime,
) : EmployeeInvitationSelfView {

    fun isExpired(clock: Clock) = expiredTimestamp <= now(clock)
}

interface EmployeeInvitationSelfView {
    val suggestedFirstName: String
    val suggestedLastName: String
}
