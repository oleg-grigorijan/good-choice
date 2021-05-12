package com.goodchoice.domain.user.model

import com.goodchoice.domain.common.model.Email

data class UserLoginRequest(val email: Email, val password: String)