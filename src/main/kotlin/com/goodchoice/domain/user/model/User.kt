package com.goodchoice.domain.user.model

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.Reference
import java.time.LocalDateTime
import java.util.*

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: Email,
    val role: UserRole,
    val passwordHash: String,
    val profileImage: Reference?,
    val createdTimeStamp: LocalDateTime
)