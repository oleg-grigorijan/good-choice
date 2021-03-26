package com.goodchoice.domain.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface Auth {

    val id: UUID
    val email: String
    val role: UserRole
}

fun Auth.requireRole(role: UserRole) {
    if (role != this.role) throw RuntimeException()
}

class SpringAuth(
    override val id: UUID,
    override val email: String,
    override val role: UserRole,
    private val passwordHash: String
) : Auth, UserDetails {

    private val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getUsername() = email
    override fun getPassword() = passwordHash

    override fun getAuthorities() = authorities
    override fun isAccountNonLocked() = true
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}