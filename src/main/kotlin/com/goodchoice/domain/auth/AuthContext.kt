package com.goodchoice.domain.auth

import org.springframework.security.core.context.SecurityContextHolder

interface AuthContext {

    val currentAuth: Auth
}

class SpringAuthContext : AuthContext {

    override val currentAuth: Auth
        get() = SecurityContextHolder.getContext().authentication.principal as Auth
}
