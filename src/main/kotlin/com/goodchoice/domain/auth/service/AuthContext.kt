package com.goodchoice.domain.auth.service

import com.goodchoice.domain.auth.model.Auth
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

interface AuthContext {

    val currentAuth: Auth
}

@Component
class SpringAuthContext : AuthContext {

    override val currentAuth: Auth
        get() = SecurityContextHolder.getContext().authentication.principal as Auth
}