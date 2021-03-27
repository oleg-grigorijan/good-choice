package com.goodchoice.domain.auth.model

import com.goodchoice.domain.common.UserRoleRequiredException
import java.util.*

// TODO(#7): Nullability
interface Auth {

    val id: UUID
    val email: String
    val role: UserRole
}

fun Auth.requireRole(expectedRole: UserRole) {
    if (expectedRole != this.role) throw UserRoleRequiredException(actual = this.role, expected = expectedRole)
}
