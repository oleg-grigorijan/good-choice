package com.goodchoice.domain.auth.model

import java.util.*

data class AuthView(
    val id: UUID,
    val email: String,
    val role: UserRole,
    val passwordHash: String
)
