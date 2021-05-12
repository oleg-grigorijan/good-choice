package com.goodchoice.domain.user.model

import com.goodchoice.domain.auth.model.UserRole

data class LoginResponse(val role: UserRole)