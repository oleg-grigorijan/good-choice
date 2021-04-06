package com.goodchoice.domain.user.model

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.model.Email
import java.time.LocalDateTime
import java.util.*

data class EmployeeInvitation(
    val id: UUID,
    val email: Email,
    val role: UserRole,
    val suggestedFirstName: String,
    val suggestedLastName: String,
    val expiredTimestamp: LocalDateTime,
) {

    fun isExpiredRelativeTo(timestamp: LocalDateTime) = expiredTimestamp <= timestamp
}

data class EmployeeInvitationSelfView(
    val suggestedFirstName: String,
    val suggestedLastName: String,
)

fun EmployeeInvitation.toSelfView() = EmployeeInvitationSelfView(
    suggestedFirstName = suggestedFirstName,
    suggestedLastName = suggestedLastName,
)
