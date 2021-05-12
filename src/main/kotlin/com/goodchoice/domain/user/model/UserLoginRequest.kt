package com.goodchoice.domain.user.model

import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.RawPassword

data class UserLoginRequest(val email: Email, val password: RawPassword)