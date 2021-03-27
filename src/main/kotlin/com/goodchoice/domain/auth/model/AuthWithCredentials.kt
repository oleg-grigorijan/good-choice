package com.goodchoice.domain.auth.model

import java.util.*

data class AuthWithCredentials(
    override val id: UUID,
    override val email: String,
    override val role: UserRole,
    val passwordHash: String
) : Auth
