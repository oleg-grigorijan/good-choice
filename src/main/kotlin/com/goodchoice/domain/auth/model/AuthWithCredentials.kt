package com.goodchoice.domain.auth.model

import com.goodchoice.domain.common.model.Email
import java.util.*

data class AuthWithCredentials(
    override val id: UUID,
    override val emailOrNull: Email,
    override val role: UserRole,
    val passwordHash: String
) : Auth
