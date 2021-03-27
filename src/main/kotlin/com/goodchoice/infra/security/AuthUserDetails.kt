package com.goodchoice.infra.security

import com.goodchoice.domain.auth.model.Auth
import com.goodchoice.domain.auth.model.AuthWithCredentials
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthUserDetails(private val authWithCredentials: AuthWithCredentials) : Auth by authWithCredentials, UserDetails {

    private val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getUsername() = authWithCredentials.email
    override fun getPassword() = authWithCredentials.passwordHash

    override fun getAuthorities() = authorities
    override fun isAccountNonLocked() = true
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
