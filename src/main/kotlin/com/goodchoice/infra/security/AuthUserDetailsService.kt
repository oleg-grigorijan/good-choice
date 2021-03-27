package com.goodchoice.infra.security

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Email
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class AuthUserDetailsService(private val authService: AuthService) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val email = Email(username)
        return authService.getCredentialsByEmailOrNull(email)
            ?.let { authWithCredentials -> AuthUserDetails(authWithCredentials) }
            ?: throw UsernameNotFoundException(username)
    }
}
