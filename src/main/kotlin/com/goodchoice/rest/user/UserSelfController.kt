package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.LoginResponse
import com.goodchoice.domain.user.model.UserLoginRequest
import com.goodchoice.domain.user.service.UserSelfService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "user-self")
class UserSelfController(private val userSelfService: UserSelfService) {

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sign in")
    fun login(@RequestBody request: UserLoginRequest): LoginResponse =
        userSelfService.login(request)
}