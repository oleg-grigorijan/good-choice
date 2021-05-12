package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.LoginResponse
import com.goodchoice.domain.user.model.UserLoginRequest

interface UserSelfService {
    fun login(userLoginRequest: UserLoginRequest): LoginResponse
}

class UserSelfServiceImpl(private val authService: AuthService) : UserSelfService {

    override fun login(userLoginRequest: UserLoginRequest): LoginResponse =
        LoginResponse(authService.getByCredentials(userLoginRequest.email, userLoginRequest.password).role)
}
