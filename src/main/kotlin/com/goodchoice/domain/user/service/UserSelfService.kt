package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.UserLoginRequest
import com.goodchoice.domain.user.model.UserRole

interface UserSelfService {
    fun login(userLoginRequest: UserLoginRequest): UserRole
}

class UserSelfServiceImpl(private val authService: AuthService) :
    UserSelfService {
    override fun login(userLoginRequest: UserLoginRequest): UserRole =
        authService.getByCredentials(userLoginRequest.email, userLoginRequest.password).let { UserRole(it.role) }
}