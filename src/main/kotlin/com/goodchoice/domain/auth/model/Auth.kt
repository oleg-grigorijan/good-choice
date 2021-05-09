package com.goodchoice.domain.auth.model

import com.goodchoice.domain.auth.ConfirmedEmailRequiredException
import com.goodchoice.domain.auth.UserRoleRequiredException
import com.goodchoice.domain.common.model.Email
import java.util.*

interface Auth {

    val id: UUID
    val emailOrNull: Email?
    val email: Email
        get() = emailOrNull ?: throw ConfirmedEmailRequiredException()
    val role: UserRole
}

fun Auth.requireRole(expectedRole: UserRole) {
    if (expectedRole != this.role) throw UserRoleRequiredException(actual = this.role, expected = setOf(expectedRole))
}

fun Auth.requireAnyRole(expectedRoles: Set<UserRole>) {
    if (!expectedRoles.contains(this.role)) throw UserRoleRequiredException(
        actual = this.role,
        expected = expectedRoles
    )
}